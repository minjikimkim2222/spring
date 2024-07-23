package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    /*
        jpa는, Entity나 Value Object만 기본적으로 반환할 수 있기에,
        나머지 DTO 같은 것들은 new 연산자를 써주어야 합니다.
     */

    public List<OrderSimpleQueryDto> findQueryDtos(){
        return em.createQuery(
                "select new jpabook.jpashop.api.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d",
                        OrderSimpleQueryDto.class)
                .getResultList();
    }

}
