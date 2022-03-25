package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    /*
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository,
                                      AccountRepository accountRepository,
                                      TransactionRepository transactionRepository,
                                      LoanRepository loanRepository,
                                      ClientLoanRepository clientLoanRepository,
                                      CardRepository cardRepository) {
        return (args) -> {
            //guardar cliente en el repositorio
            Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("holahola123"));
            clientRepository.save(client1);

            Client client2 = new Client("Francisco", "Rivera", "franciscorivera@gmail.com", passwordEncoder.encode("holahola123"));
            clientRepository.save(client2);

            Client client3 = new Client("admin", "admin", "admin@admin.com", passwordEncoder.encode("admin"));
            clientRepository.save(client3);

            //guardar cuentas en el repositorio
            Account account1 = new Account("VIN001", LocalDateTime.now(),5000, client1);
            accountRepository.save(account1);

            Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1),7500, client1);
            accountRepository.save(account2);

            Account account3 = new Account("VIN232", LocalDateTime.now(),2300, client2);
            accountRepository.save(account3);

            Account account4 = new Account("VIN324", LocalDateTime.now().plusDays(4),3000, client2);
            accountRepository.save(account4);

            Account account5 = new Account("VIN945", LocalDateTime.now(),8000, client2);
            accountRepository.save(account5);

            //asignar transacciones a distintas cuentas
            Transaction transaction1 = new Transaction(TransactionType.DEBIT, -5000, "Compra realizada...", LocalDateTime.now(), account1);
            transactionRepository.save(transaction1);

            Transaction transaction2 = new Transaction(TransactionType.CREDIT, 10030, "Transferencia recibida...", LocalDateTime.now(), account1);
            transactionRepository.save(transaction2);

            Transaction transaction3 = new Transaction(TransactionType.CREDIT, 6030, "Transferencia recibida...", LocalDateTime.now(), account2);
            transactionRepository.save(transaction3);

            Transaction transaction4 = new Transaction(TransactionType.DEBIT, -2030, "Compra realizada...", LocalDateTime.now(), account2);
            transactionRepository.save(transaction4);

            //crear prestamos
            Loan prestamo1 = new Loan("Hipotecario", 500000.000, List.of(12,24,36,48,60));
            loanRepository.save(prestamo1);

            Loan prestamo2 = new Loan("Personal", 100000.000, List.of(6,12,24));
            loanRepository.save(prestamo2);

            Loan prestamo3 = new Loan("Automotriz", 300000.000, List.of(6,12,24,36));
            loanRepository.save(prestamo3);

            //asignar prestamos a clientes
            ClientLoan prestamoCliente1 = new ClientLoan(prestamo1, client1, 400000.0, 60);
            clientLoanRepository.save(prestamoCliente1);

            ClientLoan prestamoCliente2 = new ClientLoan(prestamo2, client1, 50000.0, 12);
            clientLoanRepository.save(prestamoCliente2);

            ClientLoan prestamoCliente3 = new ClientLoan(prestamo3, client2, 34230.0, 3);
            clientLoanRepository.save(prestamoCliente3);

            ClientLoan prestamoCliente4 = new ClientLoan(prestamo1, client2, 6550.0, 24);
            clientLoanRepository.save(prestamoCliente4);

            //crear tarjetas
            Card card1 = new Card(
                    CardColor.GOLD, CardType.DEBIT, "4929-7220-8541-4927", 627,
                    client1.getFirstName() + " " + client1.getLastName(),
                    LocalDateTime.now().plusYears(5), LocalDateTime.now(), client1);
            cardRepository.save(card1);

            Card card2 = new Card(CardColor.TITANIUM, CardType.CREDIT, "4024-0071-4682-1441",
                    357, client1.getFirstName() + " " + client1.getLastName(),
                    LocalDateTime.now().plusYears(5), LocalDateTime.now(), client1);
            cardRepository.save(card2);

            Card card3 = new Card(CardColor.SILVER, CardType.CREDIT, "4532-6392-4366-7973",
                    210, client2.getFirstName() + " " + client2.getLastName(),
                    LocalDateTime.now().plusYears(5), LocalDateTime.now(), client2);
            cardRepository.save(card3);
        };
    }*/
}
