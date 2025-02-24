package com.freshworks.bambbohr.connector.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BambooHrConnectorData {



    @Setter
    private String domain;

    @Setter
    private String user;

    @Setter
    private String password;

    @Setter
    private String op;

    private Map<String, Object> operationData = new HashMap<>();

    @JsonAnySetter
    public void setOperationData(String key, Object value) {
        operationData.put(key, value);
    }

}
