package com.freshworks.bambbohr.connector.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freshworks.bambbohr.common.util.CommonUtil;
import com.freshworks.bambbohr.common.util.JsonUtil;
import com.freshworks.bambbohr.connector.dtos.*;
import com.freshworks.bambbohr.connector.request.EmployeeConnectorRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
public class BambooFSConnectorUtil {

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
                    case "create_fs_ticket": return createFSTicket(connectorData);
                    case "create_fs_user": return createFSUser(connectorData);


                    default: throw new RuntimeException("Unsupported Operation");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    private static Object createFSUser(BambooHrConnectorData connectorData) throws JsonProcessingException {

        Map<String, Object> operationData = connectorData.getOperationData();
        JsonNode jsonNode = JsonUtil.parseAsJsonNode(operationData.get("create_fs_user"));
        FreshServiceAgent freshServiceAgent = convertBambooHREmployeeTOFSUser(jsonNode);

        FSConnectionDetails fsConnectionDetails = FSConnectionDetails.builder()
                .accountDomain(jsonNode.get("fs_account").asText())
                .user(jsonNode.get("fs_user").asText())
                .password(jsonNode.get("fs_pwd").asText())
                .build();
        String auth = getAuth(fsConnectionDetails.getPassword(), fsConnectionDetails.getUser());

        return createFsAgent(List.of(freshServiceAgent), auth, fsConnectionDetails);
    }

    private static Object createFSTicket(BambooHrConnectorData connectorData) throws IOException {

        Map<String, Object> operationData = connectorData.getOperationData();
        JsonNode jsonNode = JsonUtil.parseAsJsonNode(operationData.get("create_fs_ticket"));
        FSConnectionDetails fsConnectionDetails = FSConnectionDetails.builder()
                .accountDomain(jsonNode.get("fs_account").asText())
                .user(jsonNode.get("fs_user").asText())
                .password(jsonNode.get("fs_pwd").asText())
                .build();
        JsonNode ticketsNode = jsonNode.get("tickets");

        List<String> ticketIds = new ArrayList<>();
        if(ticketsNode.isArray()){
            for(JsonNode ticketNode : ticketsNode){
                ticketIds.add(createSingleFSTicket(ticketNode, fsConnectionDetails));
            }
        }

        return ticketIds;

    }

    private static String createSingleFSTicket(JsonNode ticketNode, FSConnectionDetails fsConnectionDetails) throws IOException {

            String createTicketUrl = "https://{domain}.freshservice.com/api/v2/tickets";
            String uriString = UriComponentsBuilder.fromUriString(createTicketUrl)
                    .buildAndExpand(fsConnectionDetails.getAccountDomain())
                    .toUriString();
        FreshserviceTicket freshserviceTicket = JsonUtil.parseAsObject(ticketNode, FreshserviceTicket.class);
        Long groupId = getGroupId(ticketNode);
        Long departmentId = departmentId(ticketNode);
        freshserviceTicket.setDepartmentId(departmentId);
        freshserviceTicket.setGroupId(groupId);
        RestClient restClient = RestClient.create();
            String auth = getAuth(fsConnectionDetails.getPassword(), fsConnectionDetails.getUser());

            ResponseEntity<String> response = restClient.post()
                    .uri(uriString)
                    .header(HttpHeaders.AUTHORIZATION, "Basic "+auth)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .body(freshserviceTicket)
                    .retrieve()
                    .toEntity(String.class);

            HttpStatusCode statusCode = response.getStatusCode();
        String body = response.getBody();
        JsonNode agentBody = JsonUtil.parseAsJsonNode(body);
        JsonNode jsonNodeAgent = agentBody.get("ticket");
        return jsonNodeAgent.get("id").asText();

//        return statusCode.is2xxSuccessful() ? "Ticket Created Successfully" : "Failed to create ticket";
    }

    private static Long getGroupId(JsonNode ticketNode) {
        String type = ticketNode.get("type").asText();
        if(type.equals("laptop"))
            return 29000592980L;
        else if(type.equals("CustomerId"))
            return 29000592986L;

        return 29000592980L;
    }


    private static Long departmentId(JsonNode ticketNode) {
        String type = ticketNode.get("type").asText();
        if(type.equals("laptop"))
            return 29000324531L;
        else if(type.equals("CustomerId"))
            return 29000324530L;

        return 29000592980L;
    }


    private static Object syncBambooHRDataToFS(BambooHrConnectorData connectorData) throws JsonProcessingException {

         String employeeDirectory = fetchEmployeeDirectory(connectorData);
        JsonNode jsonNode = JsonUtil.parseAsJsonNode(employeeDirectory);
        JsonNode employeesNode = jsonNode.get("employees");
        List<FreshServiceAgent> fsAgents = new ArrayList<>();
        if(employeesNode.isArray()){
            for(JsonNode employeeNode : employeesNode){
                fsAgents.add(convertBambooHREmployeeTOFSUser(employeeNode));
            }
        }

        return createFSAgents(fsAgents , connectorData);


    }

    private static Object createFSAgents(List<FreshServiceAgent> fsAgents , BambooHrConnectorData connectorData) throws JsonProcessingException {

        Map<String, Object> operationData = connectorData.getOperationData();
        JsonNode jsonNode = JsonUtil.parseAsJsonNode(operationData.get("sync"));
        FSConnectionDetails fsConnectionDetails = FSConnectionDetails.builder()
                .accountDomain(jsonNode.get("fs_account").asText())
                .user(jsonNode.get("fs_user").asText())
                .password(jsonNode.get("fs_pwd").asText())
                .build();
        String auth = getAuth(fsConnectionDetails.getPassword(), fsConnectionDetails.getUser());

        return createFsAgent(fsAgents, auth, fsConnectionDetails);
    }

    private static Object createFsAgent(List<FreshServiceAgent> fsAgents, String auth, FSConnectionDetails fsConnectionDetails) throws JsonProcessingException {

        String createAgentUrl = "https://{domain}.freshservice.com/api/v2/agents";
        String uriString = UriComponentsBuilder.fromUriString(createAgentUrl)
                .buildAndExpand(fsConnectionDetails.getAccountDomain())
                .toUriString();
        RestClient restClient = RestClient.create();
        List<String> fsAgentIds = new ArrayList<>();
        for(FreshServiceAgent fsAgent: fsAgents){
            if(fsAgent.getFirstName().contains("Shrish")){

                JsonNode jsonNode = JsonUtil.parseAsJsonNode(fsAgent);
                ResponseEntity<String> response = restClient.post()
                        .uri(uriString)
                        .header(HttpHeaders.AUTHORIZATION, "Basic "+auth)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .header(HttpHeaders.ACCEPT, "application/json")
                        .body(jsonNode)
                        .retrieve()
                        .toEntity(String.class);

                String body = response.getBody();
                JsonNode agentBody = JsonUtil.parseAsJsonNode(body);
                JsonNode jsonNodeAgent = agentBody.get("agent");
                String id = jsonNodeAgent.get("id").asText();
                fsAgentIds.add(id);
            }

        }

        return fsAgentIds;

    }

    private static FreshServiceAgent convertBambooHREmployeeTOFSUser(JsonNode employeesNode) {

        try {
            BambooHREmployee bambooHREmployee = JsonUtil.parseAsObject(employeesNode, BambooHREmployee.class);
            return FreshServiceAgent.builder()
                    .firstName(bambooHREmployee.getFirstName())
                    .lastName(bambooHREmployee.getLastName())
                    .email(bambooHREmployee.getWorkEmail())
                    .mobileNumber(bambooHREmployee.getMobilePhone())
                    .workPhone(bambooHREmployee.getWorkPhone())
                    .departmentIds(getFSDepartment(bambooHREmployee.getDepartment()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Long> getFSDepartment(String department) {

        String s = DepartmentMapping.bambooHRToFsDepartment(department);
        return List.of(Long.parseLong(s));
    }

    private static String  fetchEmployeeDirectory(BambooHrConnectorData connectorData) {

        String fetchEmployeeDirectoryUriStr = "https://api.bamboohr.com/api/gateway.php/testfreshworks/v1/employees/directory";


        RestClient restClient = RestClient.create();
        String encodedAuth = getAuth(connectorData.getPassword(), connectorData.getUser());

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

        if(jsonNode.get("department") != null){
            String department = DepartmentMapping.fsToBambooHRDepartment(jsonNode.get("department").asText());
            if(jsonNode instanceof ObjectNode){
                ObjectNode jsonNode1 = (ObjectNode) jsonNode;
                jsonNode1.put("department", department);
            }
        }

        String uriString = UriComponentsBuilder.fromUriString(fetchEmployeeUriStr)
                .buildAndExpand(employeeId.asText())
                .toUriString();
        RestClient restClient = RestClient.create();
        String encodedAuth = getAuth(connectorData.getPassword(), connectorData.getUser());

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
            String encodedAuth = getAuth(connectorData.getPassword(), connectorData.getUser());

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

    private static String getAuth(String password, String user) {

         String auth = password+":"+user;
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
                  String encodedAuth = getAuth(connectorData.getPassword(), connectorData.getUser());

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
