package com.jotavera.demo.auth.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorDetail {
    private LocalDateTime timestamp;
    private int codigo;
    private String detail;

    public ErrorDetail(int codigo, String detail) {
        this.timestamp = LocalDateTime.now();
        this.codigo = codigo;
        this.detail = detail;
    }
}
