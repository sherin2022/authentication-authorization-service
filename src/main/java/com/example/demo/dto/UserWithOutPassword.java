package com.example.demo.dto;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserWithOutPassword {
    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String email;
    private String address;
    private Date dateOfBirth;
    private String gender;
    private String employeeId;
    private String bloodGroup;
}
