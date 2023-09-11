package com.abc.assesment.kalaha.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KalahaGameException extends RuntimeException {
    private final KalahaGameExceptionCodes exceptionCode;
    private final String message;

    public KalahaGameException(KalahaGameExceptionCodes exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.message = message;
    }
}
