package com.estafet.training.impl;

import com.estafet.training.api.AccountServiceApi;
import com.estafet.training.model.Account;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by Delcho Delov on 25/11/16.
 */
public final class DDImpl implements AccountServiceApi {
    transient private static Random rnd = new Random(4423489123001L);
    private static String NAMES[] = {"Xi Jinping", "Ma Kai", "Wang Qishan","Wang Huning","Liu Yunshan","Liu Yandong","Liu Qibao","Xu Qiliang","Sun Chunlan","Sun Zhengcai"};

    @Override
    public Account getAccountByIban(String iban) {
        System.out.println("called getAccountByIban with parameter iban = " + iban);
        return new Account(iban, NAMES[rnd.nextInt(10)], new BigDecimal(rnd.nextDouble()*10000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(), "£");
    }
}
