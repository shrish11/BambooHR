package com.freshworks.bambbohr.connector.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreshserviceTicket {

  private String description;
  private String subject;
  private int priority = 1;
  private int status = 2;  // Default status (Open)
  private int source = 2;  // Web portal source
  private String email;

  @JsonProperty("group_id")
  private Long groupId;

  @JsonProperty("department_id")
  private Long departmentId;

  @JsonProperty("workspace_id")
  private int workspaceId = 2;

  @JsonProperty("requester_id")
  private Long requestId;


}