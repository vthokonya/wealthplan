package zw.co.tayanasoft.data;

import jakarta.persistence.Entity;

@Entity
public class TransactionType extends AbstractEntity {

    private String code;
    private String name;
    private String category;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
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

}
