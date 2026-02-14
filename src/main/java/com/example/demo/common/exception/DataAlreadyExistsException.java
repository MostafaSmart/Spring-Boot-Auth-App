package com.example.demo.common.exception;

// مثال لخطأ تكرار بيانات
public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException(String message) {
        super(message);
    }
}
