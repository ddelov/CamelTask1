import java.util.List;

/**
 * Created by Delcho Delov on 21.11.2016 г..
 */
public class AccountsWrapper {
    private List<IbanSingleReportEntity> accounts;

    public List<IbanSingleReportEntity> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<IbanSingleReportEntity> accounts) {
        this.accounts = accounts;
    }
}
