package hellojpa.oneToMany;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain2 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            Member2 member = new Member2();
            member.setUsername("member1");

            em.persist(member);

            Team2 team = new Team2();
            team.setName("teamA");

            // 이 코드가 굉장히 애매해짐..
            // Team 테이블에 저장되는 값이 아님.
            // 외래키를 관리하는 연관관계의 주인인 Member테이블에 값이 들어감
            // (연관관계가 바뀌는 거니깐) -- Team 테이블 그림만 잘 살펴보면, 어디에도 연관관계 저장할 곳이 없음..
            team.getMembers().add(member);

            em.persist(team);


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
