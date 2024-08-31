package com.dividend.exception;

import lombok.Builder;
import lombok.Data;

// Error 가 발생했을 때 던져 줄 모델 클래스
@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
