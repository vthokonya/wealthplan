package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class ExchangeRate extends AbstractEntity {

    private UUID currencyId;
    private UUID primaryCurrencyId;
    private LocalDate startDate;
    private Integer exchangeRate;

    public UUID getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }
    public UUID getPrimaryCurrencyId() {
        return primaryCurrencyId;
    }
    public void setPrimaryCurrencyId(UUID primaryCurrencyId) {
        this.primaryCurrencyId = primaryCurrencyId;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public Integer getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(Integer exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

}
