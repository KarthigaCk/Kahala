package com.abc.assesment.kalaha.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class KahalaExceptionDetails {

    private final LocalDateTime timestamp = LocalDateTime.now();

    private final HttpStatus status;

    private final String message;

    private final String path;
}
