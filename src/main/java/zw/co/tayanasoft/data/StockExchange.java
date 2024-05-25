package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class StockExchange extends AbstractEntity {

    private String isoCode;
    private String name;
    private UUID currencyId;
    private boolean active;

    public String getIsoCode() {
        return isoCode;
    }
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
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
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

}
