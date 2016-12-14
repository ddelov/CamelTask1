package com.estafet.training.dao.exception;

/**
 * Created by Delcho Delov on 08/12/16.
 */
public class AccountNotFoundException extends IbanDaoException {
    public AccountNotFoundException(Exception e) {
        super(e);
    }

    public AccountNotFoundException() {
        super();
    }
}
