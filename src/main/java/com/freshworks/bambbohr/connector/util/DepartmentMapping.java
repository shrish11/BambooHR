package com.freshworks.bambbohr.connector.util;

import com.freshworks.bambbohr.connector.dtos.FSDepartment;

import java.util.List;

public class DepartmentMapping {

    public static String fsToBambooHRDepartment(String fsDeptId){

        switch (fsDeptId) {
            case "29000324527":
                return "Customer Success";
            case "29000324525":
                return "UX";
            case "29000324528":
                return "Finance";
            case "29000324529":
                return "Human Resources";
            case "29000324531":
                return "IT";
            case "29000324530":
                return "Operations";
            case "29000324526":
                return "Sales";
            default:
                return "Other";
        }
    }


    public static Long bambooHRToFsDepartment(String bambooHRDept, List<FSDepartment> departments){

        switch (bambooHRDept) {
            case "Customer Success":
                return departments.stream().filter(d -> "Customer Support".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "UX":
                return departments.stream().filter(d -> "Development".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "Finance":
                return departments.stream().filter(d -> "Finance".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "Human Resources":
                return departments.stream().filter(d -> "HR".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "IT":
                return departments.stream().filter(d -> "IT".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "Operations":
                return departments.stream().filter(d -> "Operations".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            case "Sales":
                return departments.stream().filter(d -> "Sales".equalsIgnoreCase(d.getName())).findFirst().get().getId();
            default:
                return 29000324526L;
        }
    }
}
