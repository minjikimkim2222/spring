package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /*
        주문
        -- 주문하는 회원식별자, 상품식별자, 주문수량정보를 받아서 실제 주문 엔디티 만들기
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        // 엔디티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setDeliveryStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        /*
            order 1개만 저장되어도, delivery, orderItem은 생성할 때, Cascade.ALL로 같이 생성됨
            order처럼 따로 repository로 save 안해줘도..
            (member는 생성이 아닌 조회니까 상관없음)
         */

        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔디티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }


    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {

        //return orderRepository.findAllByCriteria(orderSearch);
        return orderRepository.findAll(orderSearch); // Querydsl로 리팩토링 !!
    }
}
