package com.mindhub.homebanking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class EmailServiceContact {
    @Autowired
    private JavaMailSender javaMailSender;

    public void EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String fullName, String email, String subject, String text) throws MessagingException {
        javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("Solicitud de Contacto- " + fullName);

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
                "<div> El Cliente "+ fullName + " has solicitado que lo contacten : </div>\n" +
                "\n" +
                "<div> <table style='border:solid'>" +

                "    <tr>" +
                "        <th>Nombre: </th>" +
                "        <td>"+fullName+"</td>" +
                "    </tr>" +
                "    <tr>" +
                "        <th>Email: </th>" +
                "        <td>"+email+"</td>" +
                "    </tr>" +
                "    <tr>" +
                "        <th>Asunto: </th>" +
                "        <td>"+subject+"</td>" +
                "    </tr>" +
                "    <tr>" +
                "        <th>Solicitud: </th>" +
                "        <td>"+text+"</td>" +
                "    </tr>" +
                "</table></div>" +
                "<img src='https://raw.githubusercontent.com/CristianPFM/logoecobank/main/logoBank.png' width='150' height='100'>\n"+                "</body>\n" +
                "</html>\n";
        helper.setText(html, true);
        helper.setTo("cfuentes.ghd@gmail.com");
        javaMailSender.send(mimeMessage);
    }
}
