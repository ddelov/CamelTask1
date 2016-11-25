package com.estafet.training.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delcho Delov on 21.11.2016 г..
 */
public final class AccountsWrapper implements Serializable{
    private final List<Account> accounts = new ArrayList<Account>();

    public List<Account> getAccounts() {
        return accounts;
    }
    public void addAccount(Account entry){
        accounts.add(entry);
    }
}
