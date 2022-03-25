package com.mindhub.homebanking.utils;

import java.util.Random;

public final class CardUtils {
    private CardUtils() {

    }

    public static String getCardNumber() {
        String numTarjeta;
        numTarjeta = String.valueOf((new Random()).nextInt(5999) + 4000) + "-" +
                String.valueOf((new Random()).nextInt(9000) + 1000) + "-" +
                String.valueOf((new Random()).nextInt(9000) + 1000) + "-" +
                String.valueOf((new Random()).nextInt(9000) + 1000);
        return numTarjeta;
    }

    public static int getCvvNumber() {
        int cvv;
        cvv = (int) Math.floor(Math.random() * (999 - 100 + 1) + 100);
        return cvv;
    }
}
