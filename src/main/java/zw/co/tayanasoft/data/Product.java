package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Product extends AbstractEntity {

    private UUID ventureId;
    private String name;
    private String category;
    private boolean weight;
    private LocalDate lastCountDate;

    public UUID getVentureId() {
        return ventureId;
    }
    public void setVentureId(UUID ventureId) {
        this.ventureId = ventureId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public boolean isWeight() {
        return weight;
    }
    public void setWeight(boolean weight) {
        this.weight = weight;
    }
    public LocalDate getLastCountDate() {
        return lastCountDate;
    }
    public void setLastCountDate(LocalDate lastCountDate) {
        this.lastCountDate = lastCountDate;
    }

}
