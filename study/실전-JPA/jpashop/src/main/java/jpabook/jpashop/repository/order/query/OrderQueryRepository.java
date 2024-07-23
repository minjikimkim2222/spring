package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    // 패키지 순환 방지를 위해, OrderApiController에 있던 OrderDto 대신, query 패키지에 새로 만든 OrderQueryDto를 리턴함!
    /*
        jpa에서 리턴형이 엔디티가 아닌 dto이므로, new 연산자를 써서 아주 지저분하게..
        - 그와중에, orderItem과 같은 List는 한번에 못 넣어서, 두번에 걸쳐서 넣음. (findOrders, orderItems)

        -> 결과적으로 N+1 문제가 터짐!
            Query : 루트 1번, 컬렉션 N번
     */
    public List<OrderQueryDto> findOrderQueryDtos(){
        // 루트 조회 (toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders(); // query 1번 -> N개 (order 2건)

        // 루프를 돌면서, 컬렉션 추가 (추가 쿼리 실행)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // Query N번 (order 2건에 맞는 쿼리개수 -- result(2건)로 루프문 도니까!!)
            o.setOrderItems(orderItems);
        });

        return result;
    }

    // findOrders -- 1:N 관계 (컬렉션)을 제외한 나머지를 한번에 조회
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }

    // findOrderItems -- 1:N 관계 (컬렉션)인 orderItems 조회
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" + // OrderItem과 Item은 toOne 관계이기 때문에, 마음껏 조인해도 데이터 개수가 늘어나지 않는다!@
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();

    }

    // V5 :: JPA에서 DTO 직접 조회 -- 컬렉션 조회 최적화
    /*
        최적화
        - Query :: 루트 1번(findOrders), 컬렉션 1번 (orderItems를 where in절로 한방에 조회)
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        // 루트 조회 (toOne 코드를, 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();

        // result에서 orderIds 뽑아내기
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        // V4버전과 달리, 루프문을 돌지 않고, orderItem 컬렉션을 where in 절로 한번에 조회
        // orderId를 in절로 한방에 가져오게끔!!
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" + // OrderItem과 Item은 toOne 관계이기 때문에, 마음껏 조인해도 데이터 개수가 늘어나지 않는다!@
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 앞서 날린 한방쿼리 결과값을, Map으로 받아온 다음에..
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        // 루프를 돌면서, 컬렉션 추가 (추가 쿼리 실행 X) -- Map으로 받아온 값을 매칭시켜서 값 세팅!
        // (Map으로 받아온 값을 세팅해야지, orderItems 변수를 넣어 세팅하면, 굳이굳이 where in 절로 한방 쿼리를 넣은 이유도 없이, 쿼리가 또 나가겠지)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    /*
        V6 :: JPA에서 DTO 직접 조회 -- 플랫 데이터 최적화

     */
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, d.address, o.status, i.name, oi.orderPrice, oi.count)" +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d " +
                        "join o.orderItems oi " +
                        "join oi.item i"
                , OrderFlatDto.class
        ).getResultList();
    }
}
