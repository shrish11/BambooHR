package com.freshworks.bambbohr.connector.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class FSConnectionDetails {

    String accountDomain;
    String user;
    String password;
}
