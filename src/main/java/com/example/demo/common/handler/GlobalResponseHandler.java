package com.example.demo.common.handler;

import com.example.demo.common.dto.ApiResponse;
import com.example.demo.common.dto.ErrorDetails;
import com.example.demo.controller.IgnoreApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
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
// التحقق من وجود الـ Annotation فوق الدالة أو فوق الكلاس (Controller)
        boolean hasIgnoreAnnotation = returnType.hasMethodAnnotation(IgnoreApiResponse.class) ||
                returnType.getContainingClass().isAnnotationPresent(IgnoreApiResponse.class);

        // التحقق إذا كان الرد هو Resource
        boolean isResource = Resource.class.isAssignableFrom(returnType.getParameterType());

        // إذا كان أي منهما صحيحاً، نرجع false (أي لا تتدخل يا Handler)
        return !hasIgnoreAnnotation && !isResource;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // 1. إذا كان الرد فارغاً (void)
        if (body == null) {
            return ApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(200)
                    .message("Operation Successful")
                    .build();
        }

        // 2. إذا كان الرد أصلاً مغلفاً أو خطأ
        if (body instanceof ApiResponse || body instanceof ErrorDetails) {
            return body;
        }

        // 3. معالجة خاصة لنوع String (لأن تغليف النص مباشرة يسبب أحياناً خطأ في التحويل)
        if (body instanceof String) {
            return body;
        }

        // 4. تغليف البيانات الناجحة بشكل آلي
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Operation Successful")
                .data(body)
                .build();
    }
}