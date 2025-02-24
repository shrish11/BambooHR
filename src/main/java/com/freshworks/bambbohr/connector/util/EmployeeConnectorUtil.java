package com.freshworks.bambbohr.connector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freshworks.bambbohr.common.util.CommonUtil;
import com.freshworks.bambbohr.common.util.JsonUtil;
import com.freshworks.bambbohr.connector.dtos.BambooHrConnectorData;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;

import com.freshworks.core.traverser.net.http.HttpRequest;
import com.freshworks.core.traverser.net.http.HttpRequestResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class EmployeeConnectorUtil {

     public static Map<String,String> convertConnectorRequestDataToHagridMap(EmployeeConnectorRequest connectorRequest) {

            Map<String, String> workflowMap = new HashMap<>();
            Map<String,String> inputFromOtherConnector = new HashMap<>();
            try {
                //workflowMap = CommonUtil.convertMap(connectorRequest.getWorkflowInput());
                Map<String, Object> workflowInput = connectorRequest.getWorkflowInput();
                 String workflowInputStr = JsonUtil.toJsonString(workflowInput);
                 workflowMap.put("workflowInput", workflowInputStr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
               // inputFromOtherConnector = CommonUtil.convertMap(connectorRequest.getInputRequiredFrom());
                String inputFromOtherConnectorStr = JsonUtil.toJsonString(connectorRequest.getInputRequiredFrom());
                inputFromOtherConnector.put("inputRequiredFrom", inputFromOtherConnectorStr);
                System.out.println("stringStringMap: "+inputFromOtherConnector);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Map<String,String> taskData = new HashMap<>();

            try{
                 String taskJson = JsonUtil.toJsonString(connectorRequest.getTask());

                 taskData.put("task" , taskJson);
            }catch (Exception e) {
                 throw new RuntimeException(e);
            }

             Map<String, String> mergedMaps = CommonUtil.mergeMaps(workflowMap, inputFromOtherConnector);
             return CommonUtil.mergeMaps(taskData , mergedMaps);
        }

         public static Map<String, Object> getWorkFlowInputData(ImmutableMap<String, String> baggageMap) throws IOException {

               String workflowInput = baggageMap.get("workflowInput");
               return JsonUtil.parseAsObject(workflowInput, new TypeReference<>() {});

           }

         public static Map<String, Object> getInputFromOtherConnector(ImmutableMap<String, String> baggageMap) throws IOException {

                String inputRequiredFrom = baggageMap.get("inputRequiredFrom");
                return JsonUtil.parseAsObject(inputRequiredFrom, new TypeReference<>() {});

          }

        public static Object  callBambooHrAPI(ImmutableMap<String , String> baggageMap){

            Map<String, Object> workFlowInputData = null;
            try {
                workFlowInputData = getWorkFlowInputData(baggageMap);
                BambooHrConnectorData connectorData = getConnectorData(workFlowInputData);
                String op = connectorData.getOp();
                switch (op){
                    case "create": return createEmployeeBambooHR(connectorData);
                    case "fetch": return fetchBambooHREmployee(connectorData);
                    case "update": return updateBambooHREmployee(connectorData);
                    case "sync": return syncBambooHRDataToFS(connectorData);


                    default: throw new RuntimeException("Unsupported Operation");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    private static Object syncBambooHRDataToFS(BambooHrConnectorData connectorData) throws JsonProcessingException {

        String employeeDirectory = fetchEmployeeDirectory(connectorData);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(employeeDirectory);
        JsonNode employeesNode = jsonNode.get("employees");
        convertBambooHREmployeeTOFSUser(employeesNode);

    }

    private static void convertBambooHREmployeeTOFSUser(JsonNode employeesNode) {
    }

    private static String  fetchEmployeeDirectory(BambooHrConnectorData connectorData) {

        String fetchEmployeeDirectoryUriStr = "https://api.bamboohr.com/api/gateway.php/testfreshworks/v1/employees/directory";


        RestClient restClient = RestClient.create();
        String encodedAuth = getAuth(connectorData);

        return restClient.get()
                .uri(fetchEmployeeDirectoryUriStr)
                .header(HttpHeaders.AUTHORIZATION, "Basic "+encodedAuth)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .body(String.class);

    }

    private static Object updateBambooHREmployee(BambooHrConnectorData connectorData) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode requestBody = objectMapper.valueToTree(connectorData.getOperationData());
        JsonNode jsonNode = requestBody.get(connectorData.getOp());
        JsonNode employeeId = jsonNode.get("id");
        String fetchEmployeeUriStr = "https://api.bamboohr.com/api/gateway.php/testfreshworks/v1/employees/{id}";

        String uriString = UriComponentsBuilder.fromUriString(fetchEmployeeUriStr)
                .buildAndExpand(employeeId.asText())
                .toUriString();
        RestClient restClient = RestClient.create();
        String encodedAuth = getAuth(connectorData);

        ResponseEntity<String> response = restClient.post()
                .uri(uriString)
                .header(HttpHeaders.AUTHORIZATION, "Basic "+encodedAuth)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(jsonNode)
                .retrieve()
                .toEntity(String.class);

        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is2xxSuccessful() ? "Updated Successfully" : "Failed to update";
    }

    private static Object fetchBambooHREmployee(BambooHrConnectorData connectorData) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode requestBody = objectMapper.valueToTree(connectorData.getOperationData());
            JsonNode jsonNode = requestBody.get(connectorData.getOp());
            JsonNode employeeId = jsonNode.get("id");
            String fetchEmployeeUriStr = "https://api.bamboohr.com/api/gateway.php/testfreshworks/v1/employees/{id}?fields=firstName%2ClastName%2CemployeeNumber,gender,department%2CjobTitle%2Csupervisor%2CmobilePhone%2Cdivision%2Clocation%2CworkEmail";

            String uriString = UriComponentsBuilder.fromUriString(fetchEmployeeUriStr)
                    .buildAndExpand(employeeId.asText())
                    .toUriString();
            RestClient restClient = RestClient.create();
            String encodedAuth = getAuth(connectorData);

            return restClient.get()
                    .uri(uriString)
                    .header(HttpHeaders.AUTHORIZATION, "Basic "+encodedAuth)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .retrieve()
                    .body(String.class);


        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String getAuth(BambooHrConnectorData connectorData) {

         String auth = connectorData.getPassword()+":"+connectorData.getUser();
//        String auth = "dbe5b4ebd227cc1006ef9135b79ee8e7b8795ad7:testSR"; // Use real credentials
        return Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public static Object createEmployeeBambooHR(BambooHrConnectorData connectorData){
              try {
//                  Map<String, Object> workFlowInputData = getWorkFlowInputData(baggageMap);
//                  BambooHrConnectorData connectorData = getConnectorData(workFlowInputData);
                  RestClient restClient = RestClient.create();
                  ObjectMapper objectMapper = new ObjectMapper();
                  JsonNode requestBody = objectMapper.valueToTree(connectorData.getOperationData());
                  JsonNode jsonNode = requestBody.get(connectorData.getOp());
                  if(jsonNode instanceof ObjectNode){
                        ObjectNode objectNode = (ObjectNode) jsonNode;
                        objectNode.put("employmentHistoryStatus","Contractor");
                  }
                  String encodedAuth = getAuth(connectorData);

                  ResponseEntity<String> response = restClient.post()
                          .uri("https://api.bamboohr.com/api/gateway.php/testfreshworks/v1/employees/")
                          .header(HttpHeaders.AUTHORIZATION, "Basic "+encodedAuth)
                          .header(HttpHeaders.CONTENT_TYPE, "application/json")
                          .header(HttpHeaders.ACCEPT, "application/json")
                          .body(jsonNode)
                          .retrieve()
                          .toEntity(String.class);

                  HttpStatusCode statusCode = response.getStatusCode();
                  HttpHeaders headers = response.getHeaders();
                  String responseBody = response.getBody();
                  if(headers != null){
                        System.out.println("Headers: "+headers);
                      List<String> location = headers.get("Location");
                      System.out.println("location: "+location);
                      String locationUrl = location.get(0);
                      return locationUrl.substring(locationUrl.lastIndexOf("/") + 1);

                  }
                  else{
                      return 0;
                  }
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }

          }



    static BambooHrConnectorData getConnectorData(Map<String, Object> workflowInput){

             String jsonInput = JsonUtil.toJsonString(workflowInput.get("bamboohr_connector_new"));
             try {
                 ObjectMapper objectMapper = new ObjectMapper();
                 BambooHrConnectorData request = objectMapper.readValue(jsonInput, BambooHrConnectorData.class);

                 Map<String, Object> jsonMap = JsonUtil.parseAsObject(jsonInput, new TypeReference<>() {
                 });
                 return request;
//              BambooHrConnectorData.builder()
//                      .user(String.valueOf(Optional.ofNullable(jsonMap.get("user")).orElse("testSR")))
//                      .password(String.valueOf(Optional.ofNullable(jsonMap.get("password")).orElse("dbe5b4ebd227cc1006ef9135b79ee8e7b8795ad7")))
//                      .op(String.valueOf(Optional.ofNullable(jsonMap.get("op")).orElse("create")))
//                      .operationData(String.valueOf(Optional.ofNullable(jsonMap.get("op")).orElse("create")))
//                      .build();
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }


}
