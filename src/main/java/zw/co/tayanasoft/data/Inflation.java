package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Inflation extends AbstractEntity {

    private LocalDateTime date;
    private String type;
    private Integer inflationRate;

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getInflationRate() {
        return inflationRate;
    }
    public void setInflationRate(Integer inflationRate) {
        this.inflationRate = inflationRate;
    }

}
