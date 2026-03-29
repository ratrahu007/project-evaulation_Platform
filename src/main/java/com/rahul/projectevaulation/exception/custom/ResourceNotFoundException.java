package com.rahul.projectevaulation.exception.custom;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Thrown when an expected resource (e.g., user) cannot be found.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public ResourceNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "Resource not found");
    }
}
