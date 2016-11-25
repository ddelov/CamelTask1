package com.estafet.training.api;

import com.estafet.training.model.Account;

/**
 * Created by Delcho Delov on 24/11/16.
 */
public interface AccountServiceApi {
    Account getAccountByIban(String iban);
}
