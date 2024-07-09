package JPQL기본_심화.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class AddressExam {
    private String city;
    private String street;
    private String zipcode;

    // getters and setters

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
