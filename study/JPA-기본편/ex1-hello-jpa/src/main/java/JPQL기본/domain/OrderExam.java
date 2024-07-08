package JPQL기본.domain;

import jakarta.persistence.*;

@Entity
public class OrderExam {
    @Id @GeneratedValue
    private Long id;
    private int orderAmount;
    @Embedded
    private AddressExam addressExam;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductExam productExam;

    // getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public AddressExam getAddress() {
        return addressExam;
    }

    public void setAddress(AddressExam addressExam) {
        this.addressExam = addressExam;
    }

    public ProductExam getProduct() {
        return productExam;
    }

    public void setProduct(ProductExam product) {
        this.productExam = product;
    }
}
