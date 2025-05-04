package com.ecommerce.exceptions;

public class APIException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    String resorceName;
    String fieldName;
    String field;
    public APIException(String resorceName , String field) {
        super(String.format("%s with the name %s already exists !!!", resorceName, field));
        this.resorceName = resorceName;
        this.field = field;
    }

    public APIException(String resorceName){
        super(String.format("No %s created till now .", resorceName));
        this.resorceName = resorceName;
    }

    public APIException(String resorceName ,String fieldName ,String field ){
        super(String.format("No %s found with %s : %s ", resorceName, fieldName, field));
        this.resorceName = resorceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public APIException(){

    }
}
