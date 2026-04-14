package com.goorm.membership.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }
}
