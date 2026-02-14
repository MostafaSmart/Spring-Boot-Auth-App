package com.example.demo.common.handler;

import com.example.demo.common.dto.ApiResponse;
import com.example.demo.common.dto.ErrorDetails;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // نطبق هذا التغليف على جميع الردود
        return true; 
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 1. إذا كان الرد أصلاً عبارة عن ApiResponse أو ErrorDetails لا نغلفه مرة أخرى
        if (body instanceof ApiResponse || body instanceof ErrorDetails) {
            return body;
        }

        // 2. تغليف البيانات الناجحة بشكل آلي
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200) // يمكنك استخلاص الحالة الفعلية من response إذا أردت
                .message("Operation Successful")
                .data(body)
                .build();
    }
}