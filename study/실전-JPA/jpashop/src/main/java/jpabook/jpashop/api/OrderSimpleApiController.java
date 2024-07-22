package jpabook.jpashop.api;

import jpabook.jpashop.api.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.api.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
    xToOne (ManyToOne, OneToOne) 관계 최적화 !!

    조회할 것들
    Order
    Order -> Member (ManyToOne)
    Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /*
        V1 :: 엔디티 직접 노출
            - 양방향 연관관계가 있으면, 둘중 하나는 @JsonIgnore 해줘야 함. -> 안그러면 공포의 무한재귀 사건..
            - order -> member, order -> delivery 지연로딩이라, 실제 엔디티 대신 프록시 객체임.
              jackson 라이브러리는, 이 실제자바객체도 아닌 프록시 객체를 json으로 어떻게 생성할지 모름.. -> 예외 발생
              -> 해결책 : Hibernate5Module을 스프링 빈으로 등록하면 해결 !

            -> 근데 앞서도 말했듯이, 엔디티를 API 응답으로 노출하는 건 좋은 선택이 아니다.
               그냥 엔디티를 반환하지 말고, DTO를 반환해라 !!
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // select * from Order

        // 지연로딩으로 인해, member 객체만 보이는데, (orderItem, Delivery, member는 모두 지연로딩으로 프록시 객체라, null로 보임)
        // 근데, 강제로 member, delivery 값을 DB로부터 가져와서 보고싶어 !!
        // 프록시 객체는, "실제로 사용하게 되면", 초기화하는 과정에서 영속성 컨텍스트로부터 초기화시켜서 DB로부터 값을 가져온다..
        for (Order order : all) {
            // 프록시 객체, 강제 사용
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    /*
        V2 :: 엔디티를 DTO로 변환 (fetch join 사용 X)
            - 단점 : 지연로딩으로 쿼리 N번 호출 (N+1 문제)

        (유일한 장점은, 엔디티를 DTO로 반환하게 해서, 코드가 더 유연해지고 보기 쉬워짐..)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        // ORDER 2건 조회
        // N+1 문제
            /*
                1 + N(2개)
                -- 1 == select * from orders로 order 엔디티 조회 쿼리
                -- N == 1에서 order 엔디티 조회할 때 가져온 칼럼 개수 ==> 2개 (order 2건으로 초기데이터 세팅했음)

                -- 그러면, 매번 Dto에서 프록시객체 조회할 때마다, N번(2)만큼 추가 쿼리 발생 !!

                -- 예) 만일 order 결과가 4개면, 최악의 경우 ==> 1 + 4 + 4번 쿼리문 발생
                    (그냥 fetch join으로 한방쿼리를 발생시켜서 쿼리를 한번만 발생시킬 순 없을까?)
             */
        List<Order> orders = orderRepository.findAllByString(new OrderSearch()); // select * from orders

        List<SimpleOrderDto> result = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name; // member - name
        private LocalDateTime orderDate; // 주문시간
        private OrderStatus orderStatus;
        private Address address; // Delivery - address 임베디드타입

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // 프록시 객체 실제사용 -- 사용될때서야 쿼리 발생 !!
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // 프록시 객체 실제사용22 -- 사용될때서야 쿼리 발생 !!
        }
    }

    /*
        V3 :: 엔디티를 조회해서 DTO로 변환 (fetch join 사용 )

        - fetch join으로 쿼리 1번 호출 !! (성능 최적화)
        - fetch join으로 연관된 엔디티/컬렉션을 조회하면, 프록시가 아닌 엔디티를 가져오므로, 한방 쿼리가 날아간다!!

        참고 : fetch join -- JPA 기본편 참고
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(); // fetch join 설정!!

        List<SimpleOrderDto> result = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .collect(Collectors.toList());

        return result;
    }

    /*
        V4 :: JPA에서 DTO로 바로 조회
            - 쿼리 1번 호출 (join)
            - select 절에서 원하는 데이터만 선택해서 조회 (new!)
                -> 아예 repository가 엔디티가 아닌, DTO를 fetch join시켜서, 반환하게끔!
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findQueryDtos();
    }

}
