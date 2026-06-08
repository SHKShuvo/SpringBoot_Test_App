package com.example.test.Procedure;
import com.example.test.ResponseModel.Parameters;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import java.util.Optional;

@Service
@Slf4j
public class UserInsertProcedure {
    @PersistenceContext
    private EntityManager oEntityManager;

    public Parameters MakeUser(Parameters parameters) {
        StoredProcedureQuery oStoredProcedureQuery = oEntityManager.createStoredProcedureQuery("SHUVO.PK_USER_INSERT.PR_USER_INSERT");
        oStoredProcedureQuery.registerStoredProcedureParameter(0, String.class, ParameterMode.IN);
        oStoredProcedureQuery.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        oStoredProcedureQuery.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        oStoredProcedureQuery.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        oStoredProcedureQuery.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        oStoredProcedureQuery.registerStoredProcedureParameter(5, String.class, ParameterMode.OUT);
        oStoredProcedureQuery.registerStoredProcedureParameter(6, String.class, ParameterMode.OUT);
        oStoredProcedureQuery.registerStoredProcedureParameter(7, String.class, ParameterMode.OUT);

        log.info("parameters.getActFlg(): "+parameters.getActFlg());
        log.info("parameters.getEmail(): "+parameters.getEmail());
        log.info("parameters.getFirstName(): "+parameters.getFirstName());
        log.info("parameters.getLastName(): "+parameters.getLastName());
        log.info("parameters.getPhone(): "+parameters.getPhone());


        oStoredProcedureQuery.setParameter(0, Optional.ofNullable(parameters.getActFlg()).orElse(""));
        oStoredProcedureQuery.setParameter(1, Optional.ofNullable(parameters.getEmail()).orElse(""));
        oStoredProcedureQuery.setParameter(2, Optional.ofNullable(parameters.getFirstName()).orElse(""));
        oStoredProcedureQuery.setParameter(3, Optional.ofNullable(parameters.getLastName()).orElse(""));
        oStoredProcedureQuery.setParameter(4, Optional.ofNullable(parameters.getPhone()).orElse(""));


        oStoredProcedureQuery.execute();

        parameters.setErrorCode(String.valueOf((Integer) oStoredProcedureQuery.getOutputParameterValue(5)));
        parameters.setErrorMessage((String) oStoredProcedureQuery.getOutputParameterValue(7));
        log.info("parameters.getErrorCode(): "+parameters.getErrorCode());
        log.info("parameters.getErrorMessage(): "+parameters.getErrorMessage());
        return parameters;
    }
}
