package com.example.demo.entity.other;


import lombok.Data;

@Data
public class StringResponse {
    private String value;


    public StringResponse(String value) {
        this.value = value;
    }


}

