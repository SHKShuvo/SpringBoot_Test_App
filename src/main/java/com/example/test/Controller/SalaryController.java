package com.example.test.Controller;

import com.example.test.Entity.Salary;
import com.example.test.Service.ServiceImpl.SalaryService;
//import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/salaries")
@Tag(name = "SALARY Management", description = "SALARY Managements")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping("/batch")
    public ResponseEntity<String> saveBatch(@RequestBody List<Salary> salaryList) {
        try {
            salaryService.saveAllSalaries(salaryList);
            return ResponseEntity.ok("Salaries saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Failed to save salaries: " + e.getMessage());
        }
    }
}
