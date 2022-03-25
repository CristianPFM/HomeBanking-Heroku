package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientLoanRepository clientLoanRepository;

    @GetMapping("api/loans")
    public List<LoanDTO> getLoans() {
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(toList());
    }

    @Transactional
    @PostMapping("api/loans")
    public ResponseEntity<String> createClientLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                                             Authentication authentication
    ) {
        //obtener el cliente conectado actualmente
        Client client = clientRepository.findByEmail(authentication.getName());
        //obtener el prestamo segun el ID seleccionado en el front
        Loan loan = loanRepository.getLoanById(loanApplicationDTO.getLoanId());
        //obtener la cuenta de destino
        Account cuentaDestino = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());

        if (loanApplicationDTO.getAmount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("Monto excede el máximo que se puede solicitar para el préstamo seleccionado", HttpStatus.FORBIDDEN);

        } else if (loanApplicationDTO.getAmount() == 0) {
            return new ResponseEntity<>("El monto no puede ser 0", HttpStatus.FORBIDDEN);

        } else if (loanApplicationDTO.getPayments() == 0) {
            return new ResponseEntity<>("La cantidad de cuotas no puede ser 0", HttpStatus.FORBIDDEN);

        } else if (!loan.getPayments().contains(loanApplicationDTO.getPayments())) {
            return new ResponseEntity<>("El número de cuotas no corresponde con el prestamo solicitado", HttpStatus.FORBIDDEN);

        } else if (loan == null) {
            return new ResponseEntity<>("El prestamo solicitado no existe", HttpStatus.FORBIDDEN);

        } else if (cuentaDestino == null) {
            return new ResponseEntity<>("La cuenta de destino no existe", HttpStatus.FORBIDDEN);

        } else if (cuentaDestino.getClient() != client) {
            return new ResponseEntity<>("La cuenta origen no pertenece al usuario autenticado", HttpStatus.FORBIDDEN);

        } else {
            //crear la transacción
            transactionRepository.save(new Transaction(TransactionType.CREDIT,
                    loanApplicationDTO.getAmount()*1.2, loan.getName() + " - Loan approved", LocalDateTime.now(),
                    cuentaDestino));
            //Actualizamos el saldo de la cuenta de destino
            cuentaDestino.setBalance(cuentaDestino.getBalance() + loanApplicationDTO.getAmount());

            //asignar prestamo al cliente
            clientLoanRepository.save(new ClientLoan(loan, client, loanApplicationDTO.getAmount()*1.2, loanApplicationDTO.getPayments()));

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}
