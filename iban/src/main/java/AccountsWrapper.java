import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delcho Delov on 21.11.2016 г..
 */
public class AccountsWrapper implements Serializable{
    private final List<IbanSingleReportEntity> accounts = new ArrayList<IbanSingleReportEntity>();

    public List<IbanSingleReportEntity> getAccounts() {
        return accounts;
    }
    void addAccount(IbanSingleReportEntity entry){
        accounts.add(entry);
    }
}
