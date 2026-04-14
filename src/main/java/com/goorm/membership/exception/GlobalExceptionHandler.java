package com.goorm.membership.exception;

import com.goorm.membership.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.goorm.membership.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMemberNotFound(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("입력값이 올바르지 않습니다.", errors));
    }
}
