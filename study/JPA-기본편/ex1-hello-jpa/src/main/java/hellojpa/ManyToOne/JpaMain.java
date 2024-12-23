package hellojpa.ManyToOne;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            // 저장
            Member1 member = new Member1();
            member.setUsername("member1");
            em.persist(member);

            Team1 team = new Team1();
            team.setName("teamA");
            //-- 가짜 매핑
           // team.getMembers().add(member); // -- 주인이 아닌 연관관계ㅔ서 값 집어넣음
            member.changeTeam(team);

            em.persist(team);

            em.flush();
            em.clear();

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
