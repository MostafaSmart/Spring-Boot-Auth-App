package com.example.demo.entity.auth;

import lombok.Data;


@Data


public class LoginResponse {
    private User user;
    private String token;

    public LoginResponse(User user,String token){
        this.token= token;
        this.user=user;
    }

}

