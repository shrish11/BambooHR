
Build and Run in Local
```angular2html
1. update gradle.properties with the required nexus password
2. setup mongo DB in local or change hagrid.yal to use inmemory by setting infra_type to inmemory
2. ./gradlew clean builld -x test
3. java -jar -DtaskToDomain=task-domain-val build/libs/demo-0.0.1-SNAPSHOT.jar

```
SetUp MongoDB in Local: docker-compose.yml
```angular2html
1. create a file docker-compose.yml with below content
version: '3.7'
services:
  mongodb:
    image: mongo:latest
    container_name: mongodb_contaner
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: 12345678
      MONGO_INITDB_DATABASE: chat_app
    command:
      - '--logpath'
      - '/var/log/mongodb/mongod.log'
    ports:
      - 27017:27017
    volumes:
      - ./docker/mongodb_data:/data/db
#      - ./docker/init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js

2. Run using command: docker-compose -f docker-compose.yml up

```
```angular2html
Methods To Implement in EmployeeConnectorService
```angular2html
filterAndCreateBaggageMap : filter the Map to decide what data to send to Hagrid, if needed.
consume : calls cosumer of hagrid using abstractConsumerService.

```

Methods To Implement in Step classes in package com.freshworks.bambbohr.hagrid.steps
```angular2html

setup : to setup the step like implementing auth etc to get auth token and save using saveData("k1","v1").
 data will be same for all steps irrespective of the step hierarchy

shouldProceedWithParentObject : to check if the step should proceed with the parent object, default response: return Optional.of(Boolean.TRUE);
                                parentJsonObject === All the Parents Bean
                               JsonNode jsonNode = parentJsonObject[0].get("Key");

startSync : to start the step i.e create the HttpRequestResponse object for Hagrid, also can use getData("k1") to get the saved data
               parentJsonObject === All the Parents Bean
              JsonNode jsonNode = parentJsonObject[0].get("Status");
               getData("K1"); data set in setup method

filterResponse: implement the logic to filter the response if needed.
                 jsonNode === current Steps Complete API response.

getNextSyncRequest : achieve pagination ,to get the next request to be made to hagrid if needed
                     parentJsonObject === All the Parents Bean
                    currentRequest === current request and response details.

handleNon200ResponseCode: to handle non 200 response code.
                            currentRequest === current request and response details.

isSyncComplete: to check if the sync is complete, default put: return Optional.of(Boolean.TRUE);
                parentJsonObject === All the Parents Bean
                currentRequest === current request and response details.

parseSyncResponse: to parse the response and return it for further processing, default put: return Optional.of(jsonNode);
                     jsonNode === current Steps Complete API response.
                     Bean structure should be same as 3rd party response if we return without processing i.e return Optional.of(jsonNode);

Refer confluence: https://confluence.freshworks.com/display/PLAT/Hagrid

```
Changes Needed to do in package com.freshworks.bambbohr.hagrid.beans
```angular2html

For each bean class in the package, declare the field which is needed to be used in the Asset class.
This fields should match to the API response fields in the step class.

```
Changes Needed to do in package com.freshworks.bambbohr.hagrid.assets
```angular2html
For classes in Asset package, methods need to be written which takes Bean as parameter and map it to Asset class fields.
FreshIndex and FreshJoin Hagrid annotations can be used.
Fields which are annotated FreshIndex annotation should be used to create Expression while consuming.
```

To Run in Local using TestCase
```angular2html
 A worker test file is generated in the tests folder in package com.freshworks.bambbohr.tests under src/test/java, which can be used to test the code.
 Change hagrid.yaml to put mongoDB details in environment section.
```

To produce to UFL data from the connector
```angular2html
1. Automated Code Generation:

The following steps are automatically handled during code generation:
-Creating the MetricsClient instance
-Initializing it with baggageMap
-Setting timestamps (setStartTime, setEndTime)
-Flushing collected metrics

MetricsClient metricsClient = MetricsClientImpl.create(baggageMap);
metricsClient.setStartTime(String.valueOf(System.currentTimeMillis()));
metricsClient.setEndTime(String.valueOf(System.currentTimeMillis()));
MetricsClientImpl.flush(task.getTaskId());


2. Manual Configuration in Hygrid Steps or Beans:

The following fields must be explicitly set within the Hygrid steps or beans:
-Gauges
-Counters
-Messages
-Status

// Initialize the MetricsClient
MetricsClient metricsClient = MetricsClientImpl.create(baggageMap);

// Increment a counter
metricsClient.incrementCounter("comment_response", 2);

// Send a message with a status update
metricsClient.sendMessage("comment_response", "error");

// Set status values
metricsClient.setStatus("databaseSync", "done");
```


# BambooHR
