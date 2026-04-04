package com.J2EE.BlogNMCVC.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendVerifyEmail(String toEmail, String name, String verifyLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Xác thực email đăng ký");
        message.setText("""
                Xin chào %s,

                Cảm ơn cậu đã đăng ký tài khoản.
                Nhấn vào link bên dưới để xác thực email:

                %s

                Link xác thực có thời hạn 1 giờ.
                Nếu không phải cậu, hãy bỏ qua email này.
                """.formatted(name, verifyLink));

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String name, String verifyLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Xác thực email thay đổi mật khẩu");
        message.setText("""
                Xin chào %s,

                Có người yêu cầu thay đổi mật khẩu tài khoản trên blog NMCVC
                Nhấn vào link bên dưới để xác thực email:

                %s

                Link xác thực có thời hạn 15 phút.
                Nếu không phải cậu, hãy bỏ qua email này.
                """.formatted(name, verifyLink));

        mailSender.send(message);
    }

    public void sendLockAccountEmail(String toEmail, String name, String verifyLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Xác nhận khóa tài khoản tại blog");
        message.setText("""
                Xin chào %s,

                Có người yêu cầu khóa tài khoản trên blog NMCVC
                Nhấn vào link bên dưới để xác nhận:

                %s

                Link xác nhận có thời hạn 15 phút.
                Nếu không phải cậu, hãy bỏ qua email này.
                """.formatted(name, verifyLink));

        mailSender.send(message);
    }
}