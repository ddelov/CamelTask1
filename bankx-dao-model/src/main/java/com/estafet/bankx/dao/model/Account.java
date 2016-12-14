package com.estafet.bankx.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Known problem:
 * We do not provide locking mechanism for this experimental entity but as it is, we will get the data into the mess.
 * The user could read an account with intent to change its balance. Meanwhile a transaction could read the very same
 * account. The first transaction changes the account's balance. The second transaction changes the reporting flag
 * and as a side effect overrides the balance of the account with the old data.
 * <p>
 * Created by Yordan Nalbantov.
 */
@Entity
@Table(name = "account")
@NamedQueries({
        @NamedQuery(name = "Account.allListed",
                query = "select new com.estafet.bankx.dao.model.Account(o.iban, o.name, o.balance, o.changed) o " +
                        "from Account o where o.iban in :ibans"),
        @NamedQuery(name = "Account.changed", query = "select o from Account o where o.changed = true"),
        @NamedQuery(name = "Account.get", query = "select o from Account o where o.iban = :iban")
})
public class Account implements Serializable {

    @Id
    @Column(name = "iban")
    private String iban;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "changed")
    private Boolean changed;

    public Account() {
        this.changed = false;
    }

    public Account(Account account) {
        this.iban = account.iban;
        this.name = account.name;
        this.balance = account.balance;
        this.currency = account.currency;
        this.changed = account.changed;
    }

    public Account(String iban, String name, double balance, String currency, boolean changed) {
        this.iban = iban;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
        this.changed = changed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Account) {
            Account account = (Account) obj;
            boolean equalIban = Utils.equality(iban, account.iban);
            boolean equalName = Utils.equality(name, account.name);
            boolean equalBalance = Utils.equality(balance, balance);
            boolean equalCurrency = Utils.equality(currency, account.currency);
            boolean equalChanged = Utils.equality(changed, account.changed);
            return equalIban && equalName && equalBalance && equalCurrency && equalChanged;
        }
        return false;
    }

    /**
     * A constructor to use with JPA named queries.
     *
     * @param iban    The IBAN of the account.
     * @param name    The name of the account owner.
     * @param balance The amount of money for this account.
     * @param changed Is the account changed or not. It is used for reporting purposes.
     */
    public Account(String iban, String name, Double balance, Boolean changed) {
        this.iban = iban;
        this.name = name;
        this.balance = balance;
        this.changed = changed;
    }

    /**
     * Returns fresh accounts as detached objects read directly from the database.
     *
     * @param entityManager An EntityManager instance provided.
     * @param ibans         The IBANs of interest.
     * @return A list of detached objects for this IBANs.
     */
    public static List<Account> allListed(EntityManager entityManager, Collection<String> ibans) {
        TypedQuery<Account> query = entityManager.createNamedQuery("Account.allListed", Account.class);
        query.setParameter("ibans", ibans);
        return query.getResultList();
    }

    /**
     * Reads all changed accounts.
     *
     * @param entityManager An EntityManager instance provided.
     * @return A list of attached Account objects that are all changed.
     */
    public static List<Account> changed(EntityManager entityManager) {
        TypedQuery<Account> query = entityManager.createNamedQuery("Account.changed", Account.class);
        return query.getResultList();
    }

    /**
     * Reads an account based on its IBAN.
     *
     * @param entityManager An EntityManager instance provided.
     * @param iban          The IBAN of interest.
     * @return The corresponding Account object or null if not found.
     */
    public static Account get(EntityManager entityManager, String iban) {
        try {
            TypedQuery<Account> query = entityManager.createNamedQuery("Account.get", Account.class);
            query.setParameter("iban", iban);
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }
}