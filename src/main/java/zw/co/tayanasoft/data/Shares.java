package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class Shares extends AbstractEntity {

    private UUID stockExchangeId;
    private String ticker;
    private String name;
    private UUID sharesInIssue;
    private String industry;
    private String type;
    private Integer price;
    private boolean active;
    private boolean owned;

    public UUID getStockExchangeId() {
        return stockExchangeId;
    }
    public void setStockExchangeId(UUID stockExchangeId) {
        this.stockExchangeId = stockExchangeId;
    }
    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public UUID getSharesInIssue() {
        return sharesInIssue;
    }
    public void setSharesInIssue(UUID sharesInIssue) {
        this.sharesInIssue = sharesInIssue;
    }
    public String getIndustry() {
        return industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isOwned() {
        return owned;
    }
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

}
