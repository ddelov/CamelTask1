package com.estafet.training.model;

import java.io.Serializable;


/**
 * Immutable
 * ThreadSafe
 * Created by Delcho Delov on 21.11.2016 г.
 */
public final class Account implements Serializable{
    private String iban;
    private String name;
    private double balance;
    private String currency;

    public Account(String iban, String name, double balance, String currency) {
        this.iban = iban.replaceAll(" ", "");
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }
    public Account(){}// for JSON

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return iban.equals(account.iban);

    }

    @Override
    public int hashCode() {
        return iban.hashCode();
    }
}
