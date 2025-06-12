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

    public APIException(String resorceName ,String field ,String fieldName){
        super(String.format("No %s found with %s : %s ", resorceName, field, fieldName));
        this.resorceName = resorceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public APIException (String resorceName ,String fieldName ,Integer field){
        super(String.format("Please make an  order of the %s %s less than or equal to the available quantity %s.", resorceName, fieldName, field));
    }
    public APIException(){
    }
}
