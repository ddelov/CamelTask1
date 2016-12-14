package com.estafet.training.dao.impl;

import com.estafet.training.dao.api.IbanDaoApi;
import com.estafet.training.dao.exception.AccountNotFoundException;
import com.estafet.training.dao.exception.IbanDaoException;
import com.estafet.training.dao.exception.ParameterNotValidException;
import com.estafet.training.dao.model.AccHistory;
import com.estafet.training.dao.model.DbAccount;
import com.estafet.training.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Delcho Delov on 25/11/16.
 */
public final class IbanDaoImpl implements IbanDaoApi {
    private static final Logger log = LoggerFactory.getLogger(IbanDaoImpl.class);
    @PersistenceContext
    private EntityManager entityManager;

    public IbanDaoImpl(){

    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void insertAccount(Account account) {
        DbAccount dbAccount = new DbAccount(account);
        DbAccount found = entityManager.find(DbAccount.class, DbAccount.evaluateIdByIban(dbAccount.getIban()));
        if(found!=null){
            log.warn("Account already exist => nothing to do");
            return;
        }
        final Set<AccHistory> historySet = dbAccount.getHistRecords();
        log.info("original getHistoryRecords()" + historySet);
        if(historySet!=null){
            log.info("original getHistoryRecords().size()" + historySet.size());
            final AccHistory accHistory = historySet.iterator().next();
            log.info("original first accHistory = " + accHistory);
        }

//        EntityTransaction transaction = entityManager.getTransaction();
        try{
//            if(transaction == null || !transaction.isActive()){
//                transaction.begin();
//            }
//            entityManager.persist(dbAccount);
//            AccHistory accHistory = new AccHistory(dbAccount);
//            dbAccount.addAccHistory(accHistory);
            entityManager.persist(dbAccount);
            entityManager.flush();
            final DbAccount merge = entityManager.merge(dbAccount);
            final Set<AccHistory> historyRecords = merge.getHistRecords();
            log.info("merge.getHistoryRecords()" + historyRecords);
            if(historyRecords!=null) {
                log.info("merge.getHistoryRecords().size()" + historyRecords.size());
                final AccHistory accHistory = historyRecords.iterator().next();
                log.info("merge first accHistory = " + accHistory);
            }
            log.info("Account inserted in DB");
        }catch (Exception e){
            log.error("Could not insert account with IBAN "+ dbAccount.getIban() + ". Details: ", e);
//            transaction.setRollbackOnly();
        }finally {
//            transaction.commit();
//            log.info("Account inserted in DB");
        }
    }

    @Override
    public void updateBalance(String iban, BigDecimal newBalance) throws IbanDaoException {
        if (iban == null || newBalance == null) {
            throw new ParameterNotValidException();
        }
        iban = iban.replaceAll(" ", "");
        int id = DbAccount.evaluateIdByIban(iban);
        DbAccount account = entityManager.find(DbAccount.class, id);
        if (account == null) {
            log.warn("Account with IBAN " + iban + " was not ");
            throw new AccountNotFoundException();
        }
        if(areEquals(account.getBalance(), newBalance.doubleValue())){
            log.warn("current balance is = new balance => nothing to do");
            return;
        }
//        EntityTransaction transaction = entityManager.getTransaction();
        try {
            account.setBalance(newBalance.doubleValue());
//            AccHistory accHistory = new AccHistory(account);
            entityManager.merge(account);
//            entityManager.persist(accHistory);
        }catch (Exception e){
            log.error("Could not update balance for account with IBAN "+ iban + ". Details: "+ e.getMessage());
//            transaction.setRollbackOnly();
            throw new IbanDaoException(e);
        }finally {
//            transaction.commit();
            log.info("Account updated");
        }
    }

    @Override
    public Account findAccountByIban(String iban) throws AccountNotFoundException {
        try {
            if (iban != null && !iban.replaceAll(" ", "").isEmpty()) {
                iban = iban.replaceAll(" ", "");
                int id = DbAccount.evaluateIdByIban(iban);
                DbAccount account = entityManager.find(DbAccount.class, id);
                if(account!=null){
                    return account.getAccount();
                }
            }
        }catch (Exception e){
            log.error("Could not find account with IBAN "+ iban + ". Details: "+ e.getMessage());
            throw new AccountNotFoundException(e);
        }
        throw new AccountNotFoundException();
    }

    @Override
    public Set<Account> findAllUpdatedAfter(Calendar date) throws AccountNotFoundException {
        try {
            Query query = entityManager.createQuery(SQL_FIND_ALL_ACCOUNTS_UPDATED_AFTER);
            query.setParameter("date", new Timestamp(date.getTimeInMillis()));
            List<Account> resultList = query.getResultList();
            return new HashSet<Account>(resultList);
        }catch (Exception e){
            log.error("Could not find accounts updated since "+ date + ". Details: "+ e.getMessage());
            throw new AccountNotFoundException(e);
        }
    }
    public static boolean areEquals(double a, double b){
        return areEquals(a, b, 0.00999);
    }
    public static boolean areEquals(double a, double b, double delta){
//        double aa = Math.abs(a);
//        double ab = Math.abs(b);
        delta = Math.abs(delta);
        return a>b?Math.abs(a-b)<=delta:Math.abs(b-a)<=delta;
    }
    private static final String SQL_FIND_ALL_ACCOUNTS_UPDATED_AFTER =
            "select new com.estafet.training.model.Account(a.iban,a.name,a.balance, a.currency) from DbAccount a INNER JOIN a.histRecords ah where ah.stamp >= :date";

//    public static void main(String[] args) {
//        double a = 22.0/7.0;
//        double b = 3.1415;
//        double c = 3.1522;
//        double d = -3.1415;
//        double e = 3.1512;
//        double f = -3.145;
//        double g = -3.135;
//        assertTrue(areEquals(a, b, 0.0099));
//        assertFalse(areEquals(b, c, 0.0099));
//        assertFalse(areEquals(b, d, 0.0099));
//        assertTrue(areEquals(b, e, 0.0099));
//        assertFalse(areEquals(c, d, 0.0099));
//        assertTrue(areEquals(d, f, 0.0099));
//        assertTrue(areEquals(d, g, 0.0099));
//        assertFalse(areEquals(f, g, 0.0099));
//    }
}
