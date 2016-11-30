package com.estafet.training.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delcho Delov on 21.11.2016 г..
 */
public final class AccountsWrapper<T> implements Serializable{
    private final List<T> accounts = new ArrayList<T>();

    public List<T> getAccounts() {
        return accounts;
    }
    public void addAccount(T entry){
        accounts.add(entry);
    }
}
