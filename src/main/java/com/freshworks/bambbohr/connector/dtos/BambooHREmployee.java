package com.freshworks.bambbohr.connector.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BambooHREmployee {

    private String id;
    private String displayName;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String jobTitle;
    private String workPhone;
    private String mobilePhone;
    private String workEmail;
    private String department;
    private String location;
    private String division;
    private String facebook;
    private String linkedIn;
    private String twitterFeed;
    private String pinterest;
    private String instagram;
    private String pronouns;
    private String workPhoneExtension;
    private String supervisor;
    private Boolean photoUploaded;

    @JsonProperty("photoUrl") // In case JSON contains this key explicitly
    private String photoUrl;
}
