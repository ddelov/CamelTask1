package com.estafet.training.impl2;

import com.estafet.training.api.AccountServiceApi;
import com.estafet.training.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by Delcho Delov on 25/11/16.
 */
public final class AccountServiceImpl implements AccountServiceApi {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static String NAMES[] = {"A.I.", "M. Kai", "O. Wang","M. Praznikov","G. Delchev","H. Botev","V. Levski","M. Pi6tova","B. Ganjo","S. Karadja"};
    transient private static Random rnd = new Random(200823423001L);

    public Account getAccountByIban(String iban) {
        log.info("Implemented by com.estafet.training.impl2.AccountServiceImpl");
        return new Account(iban, NAMES[rnd.nextInt(10)], rnd.nextInt(10000), "grosha");
    }
}
