package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;

@Entity
public class TradingPlatform extends AbstractEntity {

    private String name;
    private boolean active;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

}
