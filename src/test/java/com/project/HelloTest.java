package com.project;

import com.project.modules.settings.form.PasswordForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {

    @Test
    public void password_same() {
        PasswordForm password = new PasswordForm();
        password.setNewPassword("1234");
        password.setNewPasswordConfirm("1234");

        System.out.println("password.getNewPassword().equals(password.getNewPasswordConfirm()) = " + password.getNewPassword().equals(password.getNewPasswordConfirm()));
        assertEquals(password.getNewPassword(), password.getNewPasswordConfirm());
    }
}
