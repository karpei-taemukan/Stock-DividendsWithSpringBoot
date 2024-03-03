package com.zerobase.exception.implement;

import com.zerobase.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsUserException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "이미 존재하는 사용자 명 입니다.";
    }
}
