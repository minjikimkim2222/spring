package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    /*
        검색로직 -- 동적 쿼리 고민

        해결책 1 : JPQL 쿼리 (findAllByString)
            -> but, 문자열만으로 처리한다면 에러잡기도 힘들고 개오바.. 실무에서 안씀. 패스!

        해결책 2 : JPA Criteria (findAllByCriteria)
            -> JPQL 쿼리보다는 낫지만, 여전히 실무에서 쓰기엔.. 개오바..
            -> 지금은 이대로 진행하나, 가장 멋진 해결책은 Querydsl !!
     */

    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
                            + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));

        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();
    }

    // 동적쿼리를 QueryDSL로 적용시켜보자!!
    public List<Order> findAll(OrderSearch orderSearch){
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        JPAQueryFactory query = new JPAQueryFactory(em);

        return query.select(order)
                .from(order)
                .join(order.member, member) // Order의 member를 join하는데, alias를 member로 준다는 이야기
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if (statusCond == null){
            return null;
        }

        return order.status.eq(statusCond);
    }

    private BooleanExpression nameLike(String nameCond){
        if (!StringUtils.hasText(nameCond)){
            return null;
        }

        return member.name.like(nameCond);
    }

    // fetch join -- order repository --> 쿼리문 -- '연관된' 엔디티나 컬렉션을, join fetch 시켜서, 한방쿼리로 조회가능 !!
    public List<Order> findAllWithMemberDelivery(){
        return em.createQuery(
            "select o from Order o"+
            " join fetch o.member m" +
            " join fetch o.delivery d"
        , Order.class).getResultList();
    }

    // 컬렉션 조회를 위한, fetch join
    /*
        distinct를 사용한 이유
            - 일대다 조인이 있으므로, 데이터베이스 row가 증가한다. 그결과, 같은 order 엔디티의 조회 수도 증가하게 된다.
              jpa의 distinct는 SQL에 distinct를 추가해, 조인해서 같은 엔디티(id로 식별)가 조회되면,
              어플리케이션에서 중복을 제거해준다.

            - db(sql)에서는, 칼럼 row가 완전히 똑같아야 중복제거해줘서, 여전히 2개가 아닌 4개의 결과값을 보여주지만,
              jpa에서는, from절의 Order의 id가 같으면, 중복을 제거해준다!
     */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o " +
                    "join fetch o.member m " +
                    "join fetch o.delivery d " +
                    "join fetch o.orderItems oi " +
                    "join fetch oi.item i", Order.class
        ).getResultList();
    }

    // V3.1 -- 컬렉션 조회 최적화 -- 페이징 쿼리 추가 !!
    // 0. ToOne 관계는, fetch join으로 최적화 (데이터뻥튀기가 안되서, 페이징 처리에 아무런 문제 X)
    // 1. 컬렉션은 지연로딩으로 조회 (컬렉션 조회 X)
    // 2. @BatchSize로 적용
    public List<Order> findAllWithMemberDelivery(int offset, int limit){
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m" +
                        " join fetch o.delivery d"
                , Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
        // ToOne 관계는 패치조인과 페이징 처리를 해도, 아무런 문제가 없다 !!
    }
}
