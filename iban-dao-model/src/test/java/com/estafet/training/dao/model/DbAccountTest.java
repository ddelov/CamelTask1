package com.estafet.training.dao.model;

import org.junit.Test;

import static junit.framework.TestCase.fail;

/**
 * Created by Delcho Delov on 13/12/16.
 */
public class DbAccountTest {
    @Test
    public void evaluateIdByIban() throws Exception {
        String IBAN_BASE = "BG66 ESTF 0616 0000 0000 ";
        for(int i=1; i<230; ++i){
            final int idByIban = DbAccount.evaluateIdByIban(IBAN_BASE + Integer.valueOf(i).toString());
            if(idByIban<0){
                fail();
            }
        }
    }

}