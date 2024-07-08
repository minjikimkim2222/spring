package 지연_즉시로딩.지연로딩;

import hellojpa.ManyToOne.Member1;
import hellojpa.ManyToOne.Team1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class Main1 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            Team7 team = new Team7();
            team.setName("teamA");
            em.persist(team);

            Member7 member = new Member7();
            member.setName("member1 name");
            member.setTeam(team); // team 객체를 넣었다 !!

            em.persist(member);


            em.flush();
            em.clear();

            Member7 member7 = em.find(Member7.class, member.getId());
            System.out.println("member 7 : " + member7.getTeam().getClass());

            System.out.println("-------------");
            member7.getTeam().getName(); // team에 접근하는 시점에, 쿼리문이 나갈 것 !!
            System.out.println("-------------");

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
