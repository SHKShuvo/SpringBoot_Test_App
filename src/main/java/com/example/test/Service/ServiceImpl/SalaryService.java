package com.example.test.Service.ServiceImpl;

import com.example.test.Entity.Salary;
import com.example.test.Repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 50;

    @Transactional
    public void saveAllSalaries(List<Salary> salaries) {
        for (int i = 0; i < salaries.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, salaries.size());
            List<Salary> batch = salaries.subList(i, end);

            for (Salary salary : batch) {
                if (salary.getAmount() < 0) {
                    throw new RuntimeException("Invalid salary amount: " + salary.getAmount());
                }
            }

            salaryRepository.saveAll(batch);
            salaryRepository.flush();
            entityManager.clear(); // optional: avoid memory leak
        }
    }
}
