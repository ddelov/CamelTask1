import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Random;

/**
 * Immutable
 * ThreadSafe
 * Created by Delcho Delov on 21.11.2016 г.
 */
public final class IbanSingleReportEntity implements Serializable{
    private final String iban;
    private final String name;
    private final double balance;
    private final String currency = "£";
    transient private static Random rnd = new Random(4423489123001L);

    private static String NAMES[] = {"Xi Jinping", "Ma Kai", "Wang Qishan","Wang Huning","Liu Yunshan","Liu Yandong","Liu Qibao","Xu Qiliang","Sun Chunlan","Sun Zhengcai"};

    IbanSingleReportEntity(String iban) {
        this.iban = iban;
        final BigDecimal amount = new BigDecimal(rnd.nextDouble()*10000).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.balance = amount.doubleValue();
        this.name = NAMES[rnd.nextInt(10)];
    }

    public String getName() {
        return name;
    }


    public double getBalance() {
        return balance;
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
