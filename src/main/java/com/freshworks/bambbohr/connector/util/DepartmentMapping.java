package com.freshworks.bambbohr.connector.util;

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


    public static String bambooHRToFsDepartment(String bambooHRDept){

        switch (bambooHRDept) {
            case "Customer Success":
                return "29000324527";
            case "UX":
                return "29000324525";
            case "Finance":
                return "29000324528";
            case "Human Resources":
                return "29000324529";
            case "IT":
                return "29000324531";
            case "Operations":
                return "29000324530";
            case "Sales":
                return "29000324526";
            default:
                return "29000324532";
        }
    }
}
