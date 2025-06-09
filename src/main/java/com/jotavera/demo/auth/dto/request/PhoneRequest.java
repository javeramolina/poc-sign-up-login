package com.jotavera.demo.auth.dto.request;

import lombok.Data;

@Data
public class PhoneRequest {

    private long number;

    private int citycode;

    private String contrycode;
}
