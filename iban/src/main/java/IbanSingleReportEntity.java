import java.io.Serializable;
import java.util.Random;

/**
 * Created by Delcho Delov on 21.11.2016 г.
 */
public class IbanSingleReportEntity implements Serializable{
    private final String iban;
    private String name;
    private double balance;
    private final String currency = "£";
    transient private static Random rnd = new Random(4423489123001L);

    private static String NAMES[] = {"Xi Jinping", "Ma Kai", "Wang Qishan","Wang Huning","Liu Yunshan","Liu Yandong","Liu Qibao","Xu Qiliang","Sun Chunlan","Sun Zhengcai"};

    IbanSingleReportEntity(String iban) {
        this.iban = iban;
        setBalance(rnd.nextDouble()+10000);
        this.name = NAMES[rnd.nextInt(10)];
    }

    public String getName() {
        return name;
    }


    public double getBalance() {
        return balance;
    }

    private void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIban() {
        return iban;
    }

    @Override
    public String toString() {
        return "IbanSingleReportEntity{" +
                "iban='" + iban + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                '}';
    }
}
