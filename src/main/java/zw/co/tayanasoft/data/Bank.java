package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class Bank extends AbstractEntity {

    private String name;
    private UUID currencyId;
    private Integer balance;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public UUID getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }
    public Integer getBalance() {
        return balance;
    }
    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}
