package JPQL기본_심화.JPQL기본;

import JPQL기본_심화.domain.MemberExam;
import jakarta.persistence.*;

public class JPQL조인Main4 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            // 매번 값이 들어가면 안되니깐..
//            TeamExam team = new TeamExam();
//            team.setName("teamA");
//            em.persist(team);
//
//            MemberExam member = new MemberExam();
//            member.setUsername("memberA");
//            member.setAge(10);
//            member.setTeamExam(team);
//
//            em.persist(member);


            em.flush();
            em.clear();

            // 1. 내부조인
            // -- MemberExam 과 TeamExam을 innerjoin해서, t를 쓸 수 있게 된다..
            System.out.println("내부조인 ------------");
            String innerJoin = "select m from MemberExam m inner join m.teamExam t";
            em.createQuery(innerJoin, MemberExam.class).getResultList();

            // 2. 외부조인
            System.out.println("외부조인 -------------");
            String leftOuterJoin = "select m from MemberExam m left outer join m.teamExam t";
            em.createQuery(leftOuterJoin, MemberExam.class).getResultList();

            // 3. 쎄타조인 -- 막조인
            System.out.println("쎄타조인 -------------");
            String thetaJoin = "select m from MemberExam m, TeamExam t where m.username = t.name";
            em.createQuery(thetaJoin, MemberExam.class).getResultList();

            // 4. 조인 -- on 절
            // 4-1. 조인 대상 필터링 -- 회원과 팀을 조인하면서, 팀이름이 'A'인 팀만 조인하고 싶음
            System.out.println("조인 on절 ------------ 11111 ");
            String JoinOn1 = "select m, t from MemberExam m left outer join m.teamExam t on t.name = 'teamA'";
            em.createQuery(JoinOn1).getResultList();


            // 4-2. "연관관계 없는" 엔디티 외부조인 가능 -- 회원이름과 팀이름이 같은 대상을 외부조인 -- TeamExam t으로 엔디티 따로 선언함
            System.out.println("조인 on절 ------------ 22222");
            String JoinOn2 = "select m from MemberExam m left outer join TeamExam t on m.username = t.name";
            em.createQuery(JoinOn2).getResultList();


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

