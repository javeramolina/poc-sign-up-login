package com.jotavera.demo.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PhoneDTO {
    private long number;
    private int citycode;
    private String contrycode;
}
