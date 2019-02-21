package pojo.remitta;


import javax.validation.Valid;
import java.math.BigDecimal;


public class RemittaDummyNotificationPojo {

    private String rrr;
    private BigDecimal amount;
    private String phoneNumber;
    private String email;
    private String name;

    public String getRrr() {
        return rrr;
    }

    public RemittaDummyNotificationPojo setRrr(String rrr) {
        this.rrr = rrr;
        return this;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public RemittaDummyNotificationPojo setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RemittaDummyNotificationPojo setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public RemittaDummyNotificationPojo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public RemittaDummyNotificationPojo setName(String name) {
        this.name = name;
        return this;
    }
}
