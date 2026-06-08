package com.example.test.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameters {
    private String actFlg;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String errorCode;
    private String errorMessage;
}

