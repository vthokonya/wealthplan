package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;
import java.util.UUID;

@Entity
public class Currency extends AbstractEntity {

    private String isoCode;
    private String name;
    private UUID countryId;

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
    public UUID getCountryId() {
        return countryId;
    }
    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }

}
