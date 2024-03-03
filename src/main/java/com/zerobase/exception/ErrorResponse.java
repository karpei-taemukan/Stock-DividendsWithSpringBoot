package com.zerobase.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/*
*   error 가 발생했을 때
*   던져주는 모델 클래스
* */
public class ErrorResponse {
    private int code;
    private String message;
}
