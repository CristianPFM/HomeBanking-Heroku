package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class EmailServiceOTP {
    @Autowired
    private JavaMailSender javaMailSender;

    public void EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(Client client, String otpNumber) throws MessagingException {
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("Send Otp Number - Client " + client.getFirstName() + " " + client.getLastName());

        String html = "<!doctype html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                "      xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\"\n" +
                "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                "    <title>OTP NUMBER</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div> Estimado "+ client.getFirstName() + " ingrese el siguiente codigo para aprobar la transaccion </div>\n" +
                "\n" +
                "<div style='border:solid; display:inline-block; padding:8px'> OTP NUMBER :<b>" + otpNumber + "</b></div>\n" +
                "<div><img src='https://raw.githubusercontent.com/CristianPFM/logoecobank/main/logoBank.png' width='150' height='100'><div>\n"+                "</body>\n" +
                "</html>\n";
        helper.setText(html, true);
        helper.setTo(client.getEmail());
        javaMailSender.send(mimeMessage);
    }
}
