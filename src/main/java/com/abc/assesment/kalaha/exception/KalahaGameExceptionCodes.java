package com.abc.assesment.kalaha.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
/**
 * Application specific exception status codes
 * @author Karthiga
 */
@Getter
public enum KalahaGameExceptionCodes {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    GAME_ID_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PITID_SELECTION(HttpStatus.BAD_REQUEST),
    GAME_OVER(HttpStatus.CONFLICT);
    private final HttpStatus httpStatus;

    KalahaGameExceptionCodes(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}