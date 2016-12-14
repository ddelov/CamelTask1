package com.estafet.training.dao.api;

import com.estafet.training.dao.exception.AccountNotFoundException;
import com.estafet.training.dao.exception.IbanDaoException;
import com.estafet.training.model.Account;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by Delcho Delov on 05/12/16.
 */
public interface IbanDaoApi {
    void insertAccount(final Account account) throws IbanDaoException;
    void updateBalance(final String iban, final BigDecimal newBalance) throws IbanDaoException;
    <T extends Account> T findAccountByIban(final String iban) throws AccountNotFoundException;
    Set<Account> findAllUpdatedAfter(final Calendar date) throws AccountNotFoundException;
}
