package 값타입.값타입컬렉션;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import 값타입.임베디드타입.Address;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            Member9 member = new Member9();
            member.setUsername("memberA");
            member.setHomeAddress(new Address("currentCity", "street", "1000"));

            // 값 타입 컬렉션 사용
            member.getFavoriteFoods().add("팝콘");
            member.getFavoriteFoods().add("달달이");
            member.getFavoriteFoods().add("쿠키");

            member.getAddressHistory().add(
                new Address("old1", "street", "1000"));
            member.getAddressHistory().add(
                    new Address("old2", "street", "1000"));

            em.persist(member);

            em.flush();
            em.clear(); // DB엔 데이터가 있고, 깔끔한 상태로 조회하게끔. 마치 어플리케이션 재시작처럼

            System.out.println("============ START ==========");
            Member9 findMember = em.find(Member9.class, member.getId());

            // 값 타입 컬렉션 수정 (팝콘 -> 아아)
            findMember.getFavoriteFoods().remove("팝콘");
            findMember.getFavoriteFoods().add("아아");

            // 값 타입 컬렉션 수정22 ("old1" -> "new1")
            findMember.getAddressHistory().remove(
                    new Address("old1", "street", "1000"));
                    // 이때 Address에 equals랑 hashCode가 오버라이딩되어있어야 함 !!!
            findMember.getAddressHistory().add(
                    new Address("new1", "street", "1000")
            );

            System.out.println("============ END ============");

            // entityManager 에 추가한 다음 커밋 !! (정상적일때)
            tx.commit();
        } catch (Exception e){
            // 문제가 있으면, rollback
            tx.rollback();
        } finally {
            em.close();
        }
        entityManagerFactory.close();
    }
}
