package com.example.test.ResponseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessModel {
    private String applicationId;
    private String applicationName;
    private String applicationUrl;
}
