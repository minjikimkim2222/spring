package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;

import static jpabook.jpashop.domain.Delivery.createDelivery;
import static jpabook.jpashop.domain.Member.createMember; // -- static import
import static jpabook.jpashop.domain.Order.createOrder;
import static jpabook.jpashop.domain.OrderItem.createOrderItem;
import static jpabook.jpashop.domain.item.Book.createBook;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    // InitDB 클래스 안에 또하나의 static class
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1(){
            Member member = createMember("userA", new Address("서울", "1", "1111"));
            em.persist(member); // 트랜잭션 범위 안이니깐, 영속화 ~~같이 시키면 좋지요 ~~

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1); // 이 함수가 끝나고, 트랜잭션이 반영되야 DB에 올라가기에, 아래 createOrderItem 가격란에 10000을 순순히 적었군..

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = createOrderItem(book2, 20000, 2);

            Order order = createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order); // order 엔디티에 orderItem 영속성 전이 처리를 했기에, order만 영속화시켜두 됨!!

        }

        public void dbInit2(){
            Member member = createMember("userB", new Address("제주도", "2", "222"));
            em.persist(member); // 트랜잭션 범위 안이니깐, 영속화 ~~같이 시키면 좋지요 ~~

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1); // 이 함수가 끝나고, 트랜잭션이 반영되야 DB에 올라가기에, 아래 createOrderItem 가격란에 10000을 순순히 적었군..

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = createOrderItem(book2, 40000, 4);

            Order order = createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order); // order 엔디티에 orderItem 영속성 전이 처리를 했기에, order만 영속화시켜두 됨!!

        }
    }
}

