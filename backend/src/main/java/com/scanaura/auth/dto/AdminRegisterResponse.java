package com.scanaura.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AdminRegisterResponse {

    private UUID id;
    private String fullName;
    private String email;
    private String mobile;
    private String role;
}