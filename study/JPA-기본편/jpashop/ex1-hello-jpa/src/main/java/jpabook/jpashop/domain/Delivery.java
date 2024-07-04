package jpabook.jpashop.domain;

import jakarta.persistence.*;

@Entity
public class Delivery extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    // 배송지 주소들
    private String city;
    private String street;
    private String zipcode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    // ORDERS와 양방향을 원한다면..
    @OneToOne(mappedBy = "delivery")
    private Order order;
}
