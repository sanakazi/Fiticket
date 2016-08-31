package com.fitticket.viewmodel.Validator.validations;

 
public interface Validation {

    String getErrorMessage();

    boolean isValid(String text);

}