package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID") // 연관관계 주인 -- DB 테이블의 외래키와 동일!
    private Delivery delivery;

    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    // 주문서를 중심으로 어떤 item들이 주문이 되었는지는 비즈니스 로직에 필요하니,
    // 양방향 관계 추가해보자 !!
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // -- 이 가짜매핑은 연관관계의 주인인 (OrderItem의 Order)로 인해서 매핑이 된다는 의미..
    private List<OrderItem> orderItems = new ArrayList<>();

    // getters and setters

    // -- 연관관계 편의 메서드 (양방향이라서 따로 만듦)
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); // -- 양방향 관계 모두 매핑 !! 
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
