package com.goorm.membership.dto.common;

public record ApiResponse<T>(String message, T data) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(message, data);
    }
}
