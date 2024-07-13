package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시에 가격

    private int count; // 주문 당시에 수량

    // == 생성메서드 ==
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();

        orderItem.setItem(item);
       //  orderItem.setOrder(order); -- Order 엔디티에서 연관관계편의메서드로 이미 했음 !!
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 주문되었으니까, 재고개수가 줄어야겠지요 !!

        return orderItem;
    }

    // == 비즈니스 로직 ==
    // 주문 취소
    public void cancel() {
        getItem().addStock(count); // 주문시킨 상품재고를 원복
    }

    // == 조회 로직 ==
    // 주문상품 전체 가격 조회
    public int getTotalPrice() {
        return orderPrice * count;
    }
}
