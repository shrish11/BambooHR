package com.freshworks.bambbohr.hagrid.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.freshworks.core.shared.infra.InfraDbKeyValue;
import com.freshworks.core.shared.infra.InfraDbList;
import com.freshworks.platform.optimuscore.PersistenceManager;
import com.freshworks.core.traverser.AbstractStep;
import com.freshworks.core.traverser.Annotations.FreshHierarchy;
import com.freshworks.core.traverser.ParentStep;
import com.freshworks.core.traverser.RequestResponseContainer;
import com.freshworks.core.traverser.TraverserService;
import com.freshworks.core.traverser.exception.StepFailedException;
import com.freshworks.core.traverser.net.http.HttpRequest;
import com.freshworks.core.traverser.net.http.HttpRequestResponse;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FreshHierarchy(parentClass = ParentStep.class, rateLimit = 5, duration = 5)
public class Employee extends AbstractStep {

    PersistenceManager persistenceManager;

    public Employee(InfraDbList list, InfraDbKeyValue abstractKeyValue) {
        super(list, abstractKeyValue);
    }

    /**
     * Whether a step is http or non-http based
     * @return
     */

    public Boolean isHttpBased() {

        return Boolean.TRUE;
    }

    /**
         * @param baggageMap
         * @throws StepFailedException
         */
        @Override
        public void setup(ImmutableMap<String, String> baggageMap) throws StepFailedException {
            // Implement your setup logic here if needed

            persistenceManager = new PersistenceManager(baggageMap);

        }

        /**
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional<Boolean> shouldProceedWithParentObject(JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here

            return Optional.of(Boolean.TRUE);
        }

        /**
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional startSync(JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here
            HttpRequestResponse httpRequestResponse = new HttpRequestResponse();


            return null;
        }

        /**
         * @param jsonNode
         * @throws StepFailedException
         */
        @Override
        public void filterResponse(JsonNode jsonNode) throws StepFailedException {
            // Implement your logic here if needed

        }

        /**
         * @param currentRequest
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional getNextSyncRequest(HttpRequestResponse currentRequest, JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here if needed
            return null;
        }

        /**
         * @param currentRequest
         * @return
         * @throws URISyntaxException
         * @throws StepFailedException
         */
        @Override
        public TraverserService.TraverseAction handleNon200ResponseCode(HttpRequestResponse currentRequest) throws URISyntaxException, StepFailedException {

           //write in MDC or in Document
            return null;
        }

        /**
         * @param currentRequest
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */
        @Override
        public Optional<Boolean> isSyncComplete(HttpRequestResponse currentRequest, JsonNode... parentJsonObject) throws StepFailedException {
            // Implement your logic here
            return Optional.of(Boolean.TRUE);
           }

        /**
         * @param jsonNode
         * @return
         */
        @Override
        public Optional<JsonNode> parseSyncResponse(JsonNode jsonNode) {
            // Implement your logic here
            return Optional.of(jsonNode);
        }

        /**
         *
         */
        @Override
        public void closeSync() {

        }

    /** Below are methods for non-hhp steps **/
        /**
         * @param parentJsonObject
         * @return
         * @throws StepFailedException
         */


    public void setupV2(JsonNode... parentJsonObject) throws StepFailedException {

//        saveData("dummyKey", "dummyValue");
    }

    public Boolean shouldProceedWithParentObjectV2(JsonNode... parentJsonObject) throws StepFailedException {

        return null;

    }

    public RequestResponseContainer startSyncV2(JsonNode... parentJsonObject) throws StepFailedException {

        return null;

    }

    public RequestResponseContainer execute(RequestResponseContainer currentRequestResponse, JsonNode... parentJsonObject) {

           return null;
    }

    public Boolean isSyncCompleteV2(RequestResponseContainer currentRequest, JsonNode... parentJsonObject) throws StepFailedException {

        return  null;

    }

    public JsonNode parseSyncResponseV2(RequestResponseContainer currentRequestResponse, JsonNode... parentJsonObject) {

        return null;

    }

    public TraverserService.TraverseAction handleInValidResponse(RequestResponseContainer currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    public Boolean isValidResponse(RequestResponseContainer currentRequest, JsonNode... parentJsonObject) throws StepFailedException {

        return null;
    }

    public RequestResponseContainer getNextSyncRequestV2(RequestResponseContainer currentRequest, JsonNode... parentJsonObject) throws StepFailedException {
        return null;
    }
}

