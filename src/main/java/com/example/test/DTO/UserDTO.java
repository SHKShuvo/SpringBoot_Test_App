package com.example.test.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long Id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String actFlg;
}
