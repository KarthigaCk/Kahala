package com.abc.assesment.kalaha.exception;

import com.abc.assesment.kalaha.model.KalahaExceptionDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static com.abc.assesment.kalaha.exception.KalahaGameExceptionCodes.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class KalahaExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<KalahaExceptionDetails> exceptionHandler(Exception exception, HttpServletRequest request) {
        log.error("CAUSE : {}", ExceptionUtils.getStackTrace(exception));

        return new ResponseEntity<>(new KalahaExceptionDetails(INTERNAL_SERVER_ERROR.name(),
                                                               exception.getMessage(),
                                                               request.getRequestURI()),
                                                                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(value = {KalahaGameException.class})
    public ResponseEntity<KalahaExceptionDetails> exceptionHandler(KalahaGameException exception, HttpServletRequest request) {
        log.error("CAUSE : {}", ExceptionUtils.getStackTrace(exception));

        return new ResponseEntity<>(new KalahaExceptionDetails(exception.getExceptionCode().name(),
                                                               exception.getMessage(),
                                                               request.getRequestURI()),
                                                               exception.getExceptionCode().getHttpStatus());

    }
}
