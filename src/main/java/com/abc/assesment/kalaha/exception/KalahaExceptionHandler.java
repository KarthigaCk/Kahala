package com.abc.assesment.kalaha.exception;

import com.abc.assesment.kalaha.model.KahalaExceptionDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class KalahaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<KahalaExceptionDetails> exceptionHandler(Exception exception,
                                                                            HttpServletRequest request) {
        log.error("CAUSE : {}", ExceptionUtils.getStackTrace(exception));
        return new ResponseEntity<>(new KahalaExceptionDetails(HttpStatus.INTERNAL_SERVER_ERROR,
                                                               exception.getMessage(),
                                                               request.getRequestURI()),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { KalahaGameException.class })
    public ResponseEntity<KahalaExceptionDetails> exceptionHandler(KalahaGameException exception
            , HttpServletRequest request) {

        log.error("CAUSE : {}", ExceptionUtils.getStackTrace(exception));

        return new ResponseEntity<>(new KahalaExceptionDetails(exception.getExceptionCode().getHttpStatus()
                , exception.getMessage()
                , request.getRequestURI())
                , exception.getExceptionCode().getHttpStatus());

    }
}
