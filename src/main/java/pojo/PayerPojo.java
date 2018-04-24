package pojo;

import org.hibernate.validator.constraints.NotBlank;

/**
 * CREATED BY GIBAH
 */
public class PayerPojo {
    private Long id;
    private String payerId;
    @NotBlank(message = "validation.not.null")
    private String firstName;
    @NotBlank(message = "validation.not.null")
    private String lastName;
    @NotBlank(message = "validation.not.null")
    private String email;
    @NotBlank(message = "validation.not.null")
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
