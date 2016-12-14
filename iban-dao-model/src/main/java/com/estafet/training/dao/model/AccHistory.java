package com.estafet.training.dao.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Delcho Delov on 05/12/16.
 */
@Entity
@Cacheable
@Table(name = "acc_history")
@NamedQueries({
        @NamedQuery(name = "findAccountByIban", query = "select a from account a where iban=:iban"),
//        @NamedQuery(name = "updateBalanceByIban", query = "update account set balance=:balance where iban=:iban"),
        @NamedQuery(name = "insertAccHistory", query = "insert into AccHistory(accId, balance, stamp) values(:accId, :balance, CURRENT_TIMESTAMP)"),
//    @NamedQuery(name = "findAllUpdatedAfter", query = "select ah from AccHistory ah where stamp >= :date")
})
public final class AccHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name = "acc_id")
//    @Basic(optional = false)
    @ManyToOne
//    @JoinColumn(name="id", nullable=false)
    private DbAccount account;//FK to DBAccount

    @Column(name = "balance")
    @Basic(optional=false)
    private double balance;

    @Column(name = "stamp")
    @Basic(optional=false)
    private Timestamp stamp;


    public DbAccount getAccount() { return account; }
    public AccHistory(){
        final Date date = new Date();
        this.stamp = new Timestamp(date.getTime());
    }
    public AccHistory(DbAccount account) {
        this.account = account;
        this.balance = account.getBalance();
        final Date date = new Date();
        this.stamp = new Timestamp(date.getTime());
    }

    public double getBalance() {
        return balance;
    }

    public void setAccount(DbAccount account) {
        this.account = account;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Timestamp getStamp() {
        return stamp;
    }

    public void setStamp(Timestamp stamp) {
        this.stamp = stamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccHistory that = (AccHistory) o;

        if (!account.equals(that.account)) return false;
        return stamp.equals(that.stamp);

    }

    @Override
    public int hashCode() {
        int result = account.hashCode();
        result = 31 * result + stamp.hashCode();
        return result;
    }
}
