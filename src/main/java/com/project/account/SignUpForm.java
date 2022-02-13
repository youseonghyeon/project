package com.project.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class SignUpForm {

    @Length(min = 4, max = 10)
    @NotEmpty
    private String nickname;

    @Email
    @NotEmpty
    private String email;

    @Length(min = 4, max = 10)
    @NotEmpty
    private String password;
}
