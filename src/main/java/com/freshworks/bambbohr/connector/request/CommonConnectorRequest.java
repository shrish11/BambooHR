package com.freshworks.bambbohr.connector.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Map;
import com.netflix.conductor.common.metadata.tasks.Task;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class CommonConnectorRequest {

    @NonNull
    String requestId;
    Long accountId;
    Date requestedAt = new Date();

    Map<String, Object> workflowInput;

    Map<String,Object> inputRequiredFrom;

    Task task;


}
