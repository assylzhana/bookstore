package com.micrservices.user_service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserRequest {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}
