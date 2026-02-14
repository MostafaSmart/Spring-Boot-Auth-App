package com.example.demo.entity.auth;

import lombok.Data;


@Data

public class LoginRequest {
    private String email;
    private String password;

}

