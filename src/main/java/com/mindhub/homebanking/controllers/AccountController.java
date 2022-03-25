package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("api/accounts")
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @GetMapping("api/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }

    @PostMapping("api/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        } else {
            int generacionNumCuenta = (new Random()).nextInt(900000) + 100000;
            accountRepository.save(new Account("VIN-"+String.valueOf(generacionNumCuenta), LocalDateTime.now(), 0.0, client));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @GetMapping("api/clients/current/accounts")
    public Set<Account> getData(Authentication authentication) {
        return clientRepository.findByEmail(authentication.getName()).getAccounts();
    }

/*
    @DeleteMapping ("api/account/delete")
    public ResponseEntity<Object> deleteAccount(Authentication authentication){

    }
*/

}
