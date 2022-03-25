package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Otp;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.OtpRepository;
import com.mindhub.homebanking.services.EmailServiceOTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@RestController
public class OtpController {

    @Autowired
    CardController cardController;
    @Autowired
    OtpRepository otpRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    EmailServiceOTP emailServiceOTP;



    @RequestMapping(path = "api/otp/send", method = RequestMethod.POST)
    public void sendOtp(Authentication authentication, HttpSession session) throws MessagingException, IOException {

        String otpNumber = cardController.genereteRandom(1111111,9999999);
        String idTransaction = generateUUID();
        emailServiceOTP.sendMail(clientRepository.findByEmail(authentication.getName()),otpNumber);
        otpRepository.save(new Otp(passwordEncoder.encode(otpNumber), clientRepository.findByEmail(authentication.getName()).getId(), idTransaction));
        session.setAttribute("idTransaction",idTransaction);

    }

    @RequestMapping(path = "api/otp/verify", method = RequestMethod.POST)
    public ResponseEntity<Object> verifyOtp(Authentication authentication, @RequestParam String otpNumber, HttpSession session){
        String idTransaction = (String) session.getAttribute("idTransaction");

        Otp otp = otpRepository.findOtpByClientIdAndIdTransaction(clientRepository.findByEmail(authentication.getName()).getId(),idTransaction);
        if (otp != null){
            if (passwordEncoder.matches(otpNumber, otp.getOtpNumber())){
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity<>("Check OTP number", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    public String generateUUID (){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,8);

    }

}
