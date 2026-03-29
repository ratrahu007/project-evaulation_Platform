package com.rahul.projectevaulation.exception.dto;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standardized error response payload for REST APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private String path;
    private ErrorCode errorCode;
    private Instant timestamp = Instant.now();

    public ErrorResponse(int status, String message, String path, ErrorCode errorCode) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
    }

}
