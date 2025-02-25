package com.freshworks.bambbohr.connector.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreshServiceAgent {

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    String email;

    @JsonProperty("mobile_phone_number")
    String mobileNumber;

    @JsonProperty("work_phone_number")
    String workPhone;

    @JsonProperty("department_ids")
    List<Long> departmentIds;

    Boolean occasional;

    String id;
}
