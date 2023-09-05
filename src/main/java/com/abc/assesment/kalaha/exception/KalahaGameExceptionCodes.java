package com.abc.assesment.kalaha.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum KalahaGameExceptionCodes {

    GAME_ID_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_SELECTION(HttpStatus.BAD_REQUEST),

   GAME_OVER(HttpStatus.CONFLICT) ;

    private final HttpStatus httpStatus;

    KalahaGameExceptionCodes(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
