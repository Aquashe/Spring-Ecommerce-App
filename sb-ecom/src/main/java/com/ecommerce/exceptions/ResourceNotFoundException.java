package com.ecommerce.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resorceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resorceName, String field, Long fieldId) {
        super(String.format("%s not found with %s : %s", resorceName, field, fieldId));
        this.resorceName = resorceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceNotFoundException(String resorceName, String field, String fieldName) {
        super(String.format("%s not found with %s : %s", resorceName, field, fieldName));
        this.resorceName = resorceName;
        this.field = field;
        this.fieldName = fieldName;
    }


}
