package JPQL기본_심화.JPQL심화;

import JPQL기본_심화.domain.MemberExam;
import JPQL기본_심화.domain.TeamExam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class fetchJoinMain2 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
//            TeamExam teamA = new TeamExam();
//            teamA.setName("팀A");
//            em.persist(teamA);
//
//            TeamExam teamB = new TeamExam();
//            teamB.setName("팀B");
//            em.persist(teamB);
//
//            MemberExam member1 = new MemberExam();
//            member1.setUsername("회원1");
//            member1.setTeamExam(teamA);
//            em.persist(member1);
//
//            MemberExam member2 = new MemberExam();
//            member2.setUsername("회원2");
//            member2.setTeamExam(teamA);
//            em.persist(member2);
//
//            MemberExam member3 = new MemberExam();
//            member3.setUsername("회원3");
//            member3.setTeamExam(teamB); // 팀B에 속하는 회원3
//            em.persist(member3);

            em.flush();
            em.clear();

            // 일대다, 다대다 -- 패치 조인 페이징 불가능!! -- 경고 로그를 남기고, 메모리에서 페이징 수행
//            String query = "select t from TeamExam t join fetch t.members";
//            List<TeamExam> resultList1 = em.createQuery(query, TeamExam.class)
//                    .setFirstResult(0)
//                    .setMaxResults(1)
//                    .getResultList();

            // 해결방안 1 -- 일대다 관계를 다대일 관계로 방향 변경하고 조회
            System.out.println("해결방안 1 :: 일대다 관계를 다대일 관계로 변환 ------- ");
            String query2 = "select m from MemberExam m join fetch m.teamExam";
            List<TeamExam> resultList2 = em.createQuery(query2, TeamExam.class)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getResultList();

            // 해결방안 2 -- fetch join을 제거하고, @BatchSize 어노테이션 사용
            System.out.println("해결방안 2 :: fetch join 제거 + @BatchSize 어노테이션 ------ ");
            String query3 = "select t from TeamExam t";
            List<TeamExam> resultList3 = em.createQuery(query3, TeamExam.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();
            System.out.println("result == " + resultList3.size());

            for (TeamExam teamExam : resultList3) {
                // fetch join을 제거했기에, 지연로딩으로 Member가 프록시로 불려와서, 또다시 N + 1 문제 발생..
                // 그래서 fetch join을 추가할려했는데, 일대다 매핑관계에서는, 페이징 api 쓰면 안됨!! (정합성 문제..)
                // 그래서 해결방법으로, TeamExam 엔디티에 일대다 매핑된 members 불러올 때, @BatchSize로 한꺼번에 정해진 사이즈로 불러온다 ..
                System.out.println("team :: " + teamExam.getName() + " | members :: " + teamExam.getMembers().size());
            }





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
