package com.example.demo.common.exception;

// مثال لخطأ عدم وجود بيانات
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

