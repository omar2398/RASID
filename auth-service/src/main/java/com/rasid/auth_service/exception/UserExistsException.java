package com.rasid.auth_service.exception;

public class UserExistsException extends RuntimeException{
    public UserExistsException(String msg){
        super(msg);
    }
}
