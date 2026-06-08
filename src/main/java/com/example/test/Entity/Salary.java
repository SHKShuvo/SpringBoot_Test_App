package com.example.test.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_seq")
    @SequenceGenerator(name = "salary_seq", sequenceName = "salary_seq", allocationSize = 1)
    private Long id;

    private Long employeeId;
    private Double amount;
    private LocalDate payDate;

}