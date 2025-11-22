package com.bookfair.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    
    public ErrorResponse(String message) {
        this.message = message;
        this.error = "Bad Request";
    }
}

