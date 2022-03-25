package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findOtpByClientIdAndIdTransaction(Long clientId, String idTransaction);
    void deleteOtpByClientId (Long clientId);
    }
