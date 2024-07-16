package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus; // READY (배송준비), COMP (배송중)

    // 생성 메서드 -- 배송지만 입력한 생성메서드
    public static Delivery createDelivery(Member member){
        Delivery delivery = new Delivery();

        delivery.setAddress(member.getAddress());

        return delivery;
    }
}
