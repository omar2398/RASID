package com.rasid.auth_service.exception;

import org.springframework.web.bind.annotation.PutMapping;

public class UserNotFoundWithThisEmail extends RuntimeException{
    public UserNotFoundWithThisEmail(String msg){
        super(msg);
    }
}
