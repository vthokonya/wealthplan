package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class Venture extends AbstractEntity {

    private UUID ownerId;
    private String ownerName;
    private String type;
    private UUID currencyId;

    public UUID getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public UUID getCurrencyId() {
        return currencyId;
    }
    public void setCurrencyId(UUID currencyId) {
        this.currencyId = currencyId;
    }

}
