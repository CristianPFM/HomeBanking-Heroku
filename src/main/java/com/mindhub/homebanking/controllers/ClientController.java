package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.EmailServiceChangePass;
import com.mindhub.homebanking.services.EmailServiceContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    OtpController otpController;
    @Autowired
    EmailServiceChangePass emailChangePass;
    @Autowired
    EmailServiceContact emailContact;

    @GetMapping("api/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    @GetMapping("api/clients")
    public List<ClientDTO> getClients() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @PostMapping("api/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        Client client = new Client(firstName.toLowerCase(), lastName.toLowerCase(), email.toLowerCase(), passwordEncoder.encode(password));
        clientRepository.save(client);

        int generacionNumCuenta = (new Random()).nextInt(900000) + 100000;
        Account account = new Account(
                "VIN-" + String.valueOf(generacionNumCuenta), LocalDateTime.now(), 0.0, client
        );
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("api/clients/current")
    public ClientDTO getUserAutenticated(Authentication authentication) {
        authentication.getName();
        System.out.println(authentication.getName());
        Client client = clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }

    @PostMapping("api/resetPassword")
    public ResponseEntity<Object> resetPassword(@RequestParam String name, @RequestParam String email, HttpSession session) throws MessagingException {
        if (clientRepository.findClientByEmailAndFirstName(email,name) != null) {
            String temporalPass = otpController.generateUUID();
            session.setAttribute("temporalPass", temporalPass);
            emailChangePass.sendMail(email,name,temporalPass);
            return new ResponseEntity<>("Contraseña temporal enviada", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Ha ocurrido un error al intentar recuperar la contraseña, revise los datos ingresados", HttpStatus.FORBIDDEN);
        }

    }

    @RequestMapping(value = "api/changePassword", method = RequestMethod.PUT)
    public ResponseEntity<Object> changePassword(@RequestParam String name, @RequestParam String email, @RequestParam String temporalPass, @RequestParam String newPass, HttpSession session) throws MessagingException {
        Client client = clientRepository.findClientByEmailAndFirstName(email.toLowerCase(),name.toLowerCase());
        if ( client != null && session.getAttribute("temporalPass").equals(temporalPass)) {
            client.setPassword(passwordEncoder.encode(newPass));
            clientRepository.save(client);
            return new ResponseEntity<>("Contraseña actualizada con éxito", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Ha ocurrido un error al intentar cambiar la contraseña, revise los datos ingresados", HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "api/contact", method = RequestMethod.POST)
    public ResponseEntity<Object> contactUs(@RequestParam String fullName, @RequestParam String email, @RequestParam String subject, @RequestParam String text) throws MessagingException {
            emailContact.sendMail(fullName,email,subject,text);
        return new ResponseEntity<>("Los datos han sido enviados con exito", HttpStatus.ACCEPTED);

    }
}
