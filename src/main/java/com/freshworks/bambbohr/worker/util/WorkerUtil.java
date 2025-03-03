package com.freshworks.bambbohr.worker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.freshworks.bambbohr.common.util.JsonUtil;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import com.netflix.conductor.common.metadata.tasks.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.freshworks.platform.IPCommonUtil;

@Slf4j
public class WorkerUtil {

    public static EmployeeConnectorRequest getConnectorRequest(Task task) {

               String requestId = IPCommonUtil.getNameSpace(task);

               Map<String, Object> workflowInput = new HashMap<>();
               Map<String, Object> inputData = task.getInputData();
               Long accountId = null;
               try {
                      if(inputData != null && inputData.get("workflow") != null) {
                          workflowInput = (Map<String,Object>)inputData.get("workflow");
//                          Object workflow = workflowInput.get("workflow");
//                          Map<String,Object> workFlowData = (Map<String,Object>)JsonUtil.parseAsObject(workflow, new TypeReference<>() {
//                          });
//                          Object metadatObj = workFlowData.get("metadata");
//                          Map<String,Object> metadata = (Map<String,Object>)JsonUtil.parseAsObject(metadatObj, new TypeReference<>() {
//                          });
//                          accountId = Long.parseLong(String.valueOf(metadata.get("accountId")));
                      }

                    } catch (Exception e) {

                         log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }

               Map<String, Object> inputRequiredFrom = new HashMap<>();
               try {
                       if (inputData != null && inputData.containsKey("input_required_from")) {
                               inputRequiredFrom = (Map<String, Object>) inputData.get("input_required_from");
                               System.out.println("Input Required From:");
                           }
                    } catch (Exception e) {
                          log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }
               try {
               return EmployeeConnectorRequest.
                             builder().requestId(requestId)
                             .workflowInput(workflowInput)
                             .inputRequiredFrom(inputRequiredFrom)
                             .accountId(Optional.ofNullable(task.getInputData().get("accountId"))
                                     .map(Object::toString)
                                     .map(Long::parseLong)
                                     .orElse(accountId))
                             .data(JsonUtil.parseAsJsonNode(task.getInputData().get("data")))
                             .task(task)
                             .build();
                 } catch (JsonProcessingException e) {
                         log.error(e.getMessage());
                          throw new RuntimeException(e);
                    }

           }



}
