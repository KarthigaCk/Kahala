package com.abc.assesment.kalaha.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class KalahaExceptionDetails {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;
    private final String message;
    private final String path;
}
