package com.dividend.exception.impl;

import com.dividend.exception.AbstractException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class FailedToScrapTickerException extends AbstractException {

    private final String ticker;

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getMessage() {
        return "회사 정보와 배당금 정보를 스크랩하는데 실패했습니다. -> " + ticker;
    }
}
