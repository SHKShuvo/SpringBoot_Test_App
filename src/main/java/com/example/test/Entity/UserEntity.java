package com.example.test.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS")
public class UserEntity {
    @Id
    @GeneratedValue(generator="users_id_sequence")
    @SequenceGenerator(name="users_id_sequence",
            sequenceName="USERS_ID_SEQUENCE", allocationSize=1)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String actFlg;
}
