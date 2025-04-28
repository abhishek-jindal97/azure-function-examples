package org.example.functions.model;

public record Error(
        Integer errorCode,
        String errorMessage,
        String applicationName

) {
}
