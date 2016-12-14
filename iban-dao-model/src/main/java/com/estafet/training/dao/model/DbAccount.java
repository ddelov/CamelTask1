package com.estafet.training.dao.model;

import com.estafet.training.model.Account;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Extends Account with only Id and annotate for persistence
 * Created by Delcho Delov on 05/12/16.
 */
@Entity
//@Cacheable
@Table(name = "account")
@JsonDeserialize(using = DbAccountJsonDeserializer.class)
public class DbAccount implements DaoObject{
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "iban")
    @Basic(optional=false)
    private String iban;

    @Column(name = "owner_name")
    @Basic(optional=false)
    private String name;

    @Column(name = "balance")
    @Basic(optional=false)
    private double balance;

    @Column(name = "currency")
    @Basic(optional=false)
    private String currency;

    @OneToMany(cascade={CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy="account", orphanRemoval = true)
    private Set<AccHistory> histRecords;

//    public DbAccount(){}

    public DbAccount(Account account) {
        this.iban=account.getIban().replaceAll(" ", "");
        this.name=account.getName();
        this.currency = account.getCurrency();
        this.id = evaluateIdByIban(account.getIban());
        setBalance(account.getBalance());
    }
    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        //        this.balance = account.getBalance();
        //insert history record
        if(getHistRecords()==null) {
            histRecords = new HashSet<>(1);
        }
        final AccHistory accHistory = new AccHistory();
        accHistory.setBalance(balance);
        accHistory.setAccount(this);
        histRecords.add(accHistory);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Account getAccount() {
        return new Account(getIban(), getName(), getBalance(), getCurrency());
    }

    public Set<AccHistory> getHistRecords() {
        return histRecords;
    }

    public void setHistRecords(Set<AccHistory> histRecords) {
        this.histRecords = histRecords;
    }

    public static int evaluateIdByIban(String iban){
        if(iban==null || iban.isEmpty()){
            return 0;
        }
        iban = iban.replaceAll(" ", "");
        BigInteger bigInteger = BigInteger.ZERO;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"/*"SHA-256"*/);
            md.update(iban.getBytes());
            byte[] digest = md.digest();
            bigInteger = new BigInteger(digest);
        } catch (NoSuchAlgorithmException ignored) {}
        //make sure it's > 0
        final int intValue = bigInteger.intValue();
        return  intValue < 0? Math.abs(intValue):intValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
