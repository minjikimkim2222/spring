package jpabook.jpashop.api;

import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /*
        V1 :: 엔디티 직접 노출 (re)
            - 엔디티가 변하면 API 스펙이 변한다
            - 트랜잭션 안에서, 지연로딩 강제 초기화 필요
            - 양방향 연관관계 문제 -> @JsonIgnore
            - Hibernate5Module 모듈 등록, LAZY=null 처리
                jackson 라이브러리는, 프록시 객체를 json으로 어떻게 생성할지 모름.
                Hibernate의 LAZY 프록시 객체는 기본적으로 null로 처리해서, 무시하게끔 설정.
                    (Hibernate5Module을 등록하면, LAZY 로딩된 프록시객체를 JSON으로 변환할 때, null로 처리하게끔 할 수 있음)
                이후에 프록시 객체가 초기화되면, 프록시 객체의 target 포인터로, 실제 자바 객체에 접근가능할 때 값을 가져올 수 있게끔..)

            -> 마찬가지로, 엔디티를 직접 노출하므로, 좋은 방법은 아닙니다!!
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());// select * from orders o

        // lazyloading은 한번 초기화해줘야, 쿼리를 발생시킨다!
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // 컬렉션 조회 -- 일대다 조회 -- Lazy 강제 초기화
        }

        return all;
    }

    /*
        V2 :: 엔디티를 DTO로 변환

        - 지연로딩으로 너무 많은 SQL 실행
        - SQL 실행 수
            order 1번
            member, address N번 (order 조회 수만큼)
            orderItem N번 (order 조회 수만큼)
            item N번 ('orderItem' 조회 수만큼)
         - 오마이갓.. 쿼리가 너무 많이 나가요 ㅜㅜ
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    @Data
    static class OrderDto {
        private Long orderId;
        private String name; // Member - name
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // Delivery - address

        private List<OrderItemDto> orderItems; // OrderItem - 일대다 매핑 추가 !!

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // 프록시 객체 강제 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // 프록시 객체 강제 초기화
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem)) // 프록시 객체 강제초기화 (컬렉션 조회)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName(); // 프록시 객체 강제 초기화
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

    /*
        V3 :: 엔디티를 DTO로 변환 - 패치 조인 최적화 적용

        (+) - 패치조인으로 SQL이 1번만 실행된 건, 너무 좋아요
        (-) - 다만, 컬렉션 패치조인을 사용하면, '페이징이 불가능해진다'!!
                (일대다를 패치조인하는 순간, 페이징 쿼리가 아예 나가지 않습니다!!)
                (일대다로 패치조인하는 순간, 데이터 뻥튀기가 되어버려, 데이터정합성에 문제가 발생한다)
                    기대: order 2건, 실제: order 4건)
        (-) - 둘 이상의 컬렉션은 패치조인이 불가능해진다!
              (1:N은 데이터 뻥튀기가 발생하는데, 둘이상의 컬렉션에 패치 조인을 사용하게 되면,
              일대다가 아니고, 일대다의 다 -- 데이터뻥튀기가 (1:N 1:M -> 조인, N*M)
              데이터개수가 많아지는 것뿐만 아니라, 어떤 것을 기준으로 조인할지도 모르겠음..
              데이터정합성에 문제가 발생할 수 있다.)

     */

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /*
        V3.1 -- 엔디티를 조회해서 DTO로 변환 -- 페이징 고려

        - ToOne 관계만, 우선 패치조인으로 최적화
        - 컬렉션 관계(OneToMany)는, 지연로딩으로 조회 (fetch join X -- 페이징 쿼리 안됨)
           -> hibernate.default_batch_fetch_size, @BatchSize로 최적화

        -> 쿼리최적화가 된다! (1 + N + M -> 1 + 1 + 1 쿼리로 최적화된다!)
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset, // offset -- 몇번째 데이터부터..
            @RequestParam(value = "limit", defaultValue = "100") int limit){ // limit -- 데이터 몇개씩 가져올지..
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

        return result;
    }

    /*
        V4 -- JPA에서 DTO 직접 조회

        -- 이전까지는 JPA의 orderRepository에서 dto가 아닌 도메인을 반환하게 한후, map 스트림으로 dto로 변환시킨 후, 반환하게 했다.
        -- 사실, repository에는 엔디티 자체를 반환하는 것이 좋기에,
            jpa에서 엔디티가 아닌 dto를 직접 반환하게끔 하려면, 따로 패키지를 만드는 것이 좋다.

        --> 결과적으로, N+1 문제가 터짐!
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    /*
        V5 :: JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화

        - ToOne 관계들은 먼저 조회하고,
          여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItems를 한꺼번에 조회

        - Map을 이용해서, 매칭성능향상
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    /*
        V6 :: jpa에서 dto 직접 조회 -- 플랫 데이터 최적화

        - 장점 : 쿼리 1번

        - 단점 :
            -> 쿼리는 한번이지만, 조인으로 인해 DB에서 어플리케이션에 전달하는 데이터에 중복데이터가 추가되므로,
               (일대다 조인이면, 데이터 뻥튀기가 된다고 했자나!!)
               상황에 따라 V5보다 더 느릴 수도 있다.
            -> 페이징 불가능 (일대다, 데이터 뻥튀기 !!)
     */
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(){
        return orderQueryRepository.findAllByDto_flat();
    }
}
