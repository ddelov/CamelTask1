package com.estafet.training.model;

import java.io.Serializable;

/**
 * Immutable
 * ThreadSafe
 * Created by Delcho Delov on 21.11.2016 г.
 */
public final class Account implements Serializable{
    private final String iban;
    private final String name;
    private final double balance;
    private final String currency;

    public Account(String iban, String name, double balance, String currency) {
        this.iban = iban;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }


    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIban() {
        return iban;
    }

    @Override
    public String toString() {
        return "com.estafet.training.Account{" +
                "iban='" + iban + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                '}';
    }
}
