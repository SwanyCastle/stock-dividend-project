package com.dividend.exception.impl;

import com.dividend.exception.AbstractException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class UserNotFoundException extends AbstractException {

    private final String username;

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 사용자 입니다. -> " + username;
    }
}
