package com.project.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "test"})
@Component
public class ConsoleEmailService implements EmailService{

    @Override
    public void send(EmailMessage emailMessage) {
//        log.info("sent email: {}", emailMessage.getMessage());\
        log.info("가입 인증 이메일 전송");
    }
}

