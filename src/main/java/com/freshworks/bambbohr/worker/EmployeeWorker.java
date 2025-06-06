package com.freshworks.bambbohr.worker;


import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.freshworks.bambbohr.connector.service.EmployeeConnectorService;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshworks.bambbohr.connector.service.ConnectorHagridConfiguration;
import lombok.extern.slf4j.Slf4j;
import com.freshworks.bambbohr.connector.util.BambooFSConnectorUtil;
import com.freshworks.bambbohr.worker.util.WorkerUtil;



@Slf4j
@Component
public class EmployeeWorker {

   private final TaskClient httpTaskClient;
   private final EmployeeConnectorService employeeConnectorService;

   ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EmployeeWorker(TaskClient httpTaskClient , EmployeeConnectorService employeeConnectorService) {
        this.httpTaskClient = httpTaskClient;
        this.employeeConnectorService = employeeConnectorService;
    }


    @WorkerTask(value = "create_fs_user_connector")
    public TaskResult work(Task task) {

            log.info("worker task: create_fs_user_connector invoked");

            EmployeeConnectorRequest employeeConnectorRequest = WorkerUtil.getConnectorRequest(task);
            ConnectorHagridConfiguration connectorHagridConfiguration = new ConnectorHagridConfiguration();
            Map<String, String> inputData = BambooFSConnectorUtil.convertConnectorRequestDataToHagridMap(employeeConnectorRequest);

            ImmutableMap<String , String> baggageMap = employeeConnectorService.filterAndCreateBaggageMap(inputData);

            Object employeeBambooHR = BambooFSConnectorUtil.callBambooHrAPI(baggageMap);

        Map<String,Object> output = new HashMap();
        output.put("data", employeeBambooHR);



//            MetricsClient metricsClient = MetricsClientImpl.create(baggageMap);


//            employeeConnectorService.startSync(baggageMap, employeeConnectorRequest , connectorHagridConfiguration);
//            metricsClient.setStartTime(String.valueOf(System.currentTimeMillis()));


//             while(!jiraissueConnectorService.isSyncComplete(connectorHagridConfiguration)) {
//
//              }

//             Map<String,Object> output = employeeConnectorService.consume(employeeConnectorRequest , connectorHagridConfiguration);



        task.setStatus(Task.Status.COMPLETED);
//        metricsClient.setEndTime(String.valueOf(System.currentTimeMillis()));
//        MetricsClientImpl.flush(task.getTaskId());
        task.setOutputData(output);
        System.out.println("invoked");
//        employeeConnectorService.clearData(connectorHagridConfiguration);
        return new TaskResult(task);
    }

//    @WorkerTask(value = "fs_create_ticket")
//    public TaskResult workSync(Task task) {
//
//        System.out.println("fs_create_ticket invoked");
//
//        EmployeeConnectorRequest employeeConnectorRequest = WorkerUtil.getConnectorRequest(task);
//        ConnectorHagridConfiguration connectorHagridConfiguration = new ConnectorHagridConfiguration();
//        Map<String, String> inputData = EmployeeConnectorUtil.convertConnectorRequestDataToHagridMap(employeeConnectorRequest);
//        ImmutableMap<String , String> baggageMap = employeeConnectorService.filterAndCreateBaggageMap(inputData);
//        Object fsTicket = FreshServiceUtil.createFSTicket(baggageMap);
//
//        Map<String,Object> output = new HashMap();
//        output.put("data", fsTicket);
//        task.setStatus(Task.Status.COMPLETED);
//        task.setOutputData(output);
//
//        return new TaskResult(task);
//    }






    }


