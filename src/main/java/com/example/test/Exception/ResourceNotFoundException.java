package com.example.test.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private long fieldValue;
    private String fieldValue1;

    public ResourceNotFoundException(String resourceName, String fieldName, long fieldValue) {

        super(String.format("%s not found with %s : '%s'", resourceName,fieldName,fieldValue));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue1) {

        super(String.format("%s not found with %s : '%s'", resourceName,fieldName,fieldValue1));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue1 = fieldValue1;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public long getFieldValue() {
        return fieldValue;
    }

    public String getFieldValue1() {
        return fieldValue1;
    }
}
