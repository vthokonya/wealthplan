package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;

@Entity
public class Country extends AbstractEntity {

    private String isoCode;
    private String countryName;

    public String getIsoCode() {
        return isoCode;
    }
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

}
