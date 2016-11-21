/**
 * Created by Delcho Delov on 21.11.2016 г.
 */
class IbanSingleReportEntity {
    private String iban;
    private String name;
    private double balance;
    private String currency;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIban() {

        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
