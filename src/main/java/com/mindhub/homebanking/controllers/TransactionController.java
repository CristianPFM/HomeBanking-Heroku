package com.mindhub.homebanking.controllers;

import com.lowagie.text.DocumentException;
import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.OtpRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OtpRepository otpRepository;


    @GetMapping("api/transactions")
    public List<TransactionDTO> getTransactions() {
        return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(toList());
    }

    @GetMapping("api/transactions/{id}")
    public TransactionDTO getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id).map(TransactionDTO::new).orElse(null);
    }

    @Transactional
    @PostMapping("api/transactions")
    public ResponseEntity<Object> makeTransaction(
            @RequestParam Double amount, @RequestParam String description,
            @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
            Authentication authentication, HttpSession httpSession
    ) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account cuentaOrigen = accountRepository.findByNumber(fromAccountNumber);
        Account cuentaDestino = accountRepository.findByNumber(toAccountNumber);

        //Verificamos si todos los datos vienen correctamente, en caso contrario se devuelve un error
        if (amount.isNaN() || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty() ||
                fromAccountNumber.equals(toAccountNumber) || cuentaOrigen == null || cuentaDestino == null ||
                cuentaOrigen.getBalance() < amount || cuentaOrigen.getClient() != client
        ) {
            return new ResponseEntity<>("Datos incompletos, verificar", HttpStatus.FORBIDDEN);
        } else {
            Transaction debit = (new Transaction(TransactionType.DEBIT, -amount,
                    description + " " + toAccountNumber, LocalDateTime.now(), cuentaOrigen));
            transactionRepository.save(debit);

            //Actualizamos el saldo de la cuenta origen
            cuentaOrigen.setBalance(cuentaOrigen.getBalance() - amount);

            transactionRepository.save(new Transaction(TransactionType.CREDIT, amount,
                    description + " " + fromAccountNumber, LocalDateTime.now(), cuentaDestino));
            //Actualizamos el saldo de la cuenta de destino
            cuentaDestino.setBalance(cuentaDestino.getBalance() + amount);

            //Borramos el OTP
            otpRepository.deleteOtpByClientId(clientRepository.findByEmail(authentication.getName()).getId());

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @GetMapping ("api/transactions/export/pdf/{number}")
    public void exportToPDF(@PathVariable String number, HttpServletResponse response,
                            Authentication authentication) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findByNumber(number);

        System.out.println("number: " + number);

        //Account accountDTO = accountRepository.findByNumber("VIN001");
        List<TransactionDTO> listTransactions = transactionRepository.findTransactionByAccountId(account.getId()).stream().map(TransactionDTO::new).collect(toList());

        UserPDFExporter exporter = new UserPDFExporter(account, listTransactions, client);
        exporter.export(response);
    }
}
