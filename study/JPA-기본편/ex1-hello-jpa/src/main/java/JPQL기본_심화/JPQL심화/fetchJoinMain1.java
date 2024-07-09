package JPQL기본_심화.JPQL심화;

import JPQL기본_심화.domain.MemberExam;
import JPQL기본_심화.domain.TeamExam;
import jakarta.persistence.*;

import java.util.List;

public class fetchJoinMain1 {
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

            // 회원조회 (기본 select문)
            String query = "select m from MemberExam m";
            List<MemberExam> resultList = em.createQuery(query, MemberExam.class).getResultList();

            for (MemberExam memberExam : resultList) {
                System.out.println("member : " + memberExam.getUsername()
                        // MemberExam의 TeamExam과의 관계설정시, 지연로딩 설정해두어서, TeamExam은 프록시로 들어와서, 실제로 사용될때마다 쿼리문이 날라갈 것 !
                                            + ", " + memberExam.getTeamExam().getName());
            }

            // 회원1, 팀A (DB에 SQL 날려서 조회)
            // 회원2, 팀B (DB에 SQL 안날리고, 1차캐시에서 조회)
            // 회원3, 팀C (DB에 SQL 날려서 조회)

            // 최악의 경우, 팀소속이 다 다를경우, 팀마다 쿼리문이 추가로 나갈 것 !!
            // 만약, 회원 100명 .? => N + 1 -> oh no..

            // 해결책 ::: fetch join을 사용하면, 연관된 엔디티나 컬렉션을, '한방쿼리'를 통해 조회할 수 있다 !!!
            System.out.println("fetch join 실행 ::::::::::::::::::: ");
            String fetchJoinQuery = "select m from MemberExam m join fetch m.teamExam";
                // 내가 MemberExam m만 설정했는데, 쿼리문보니까 teamExam도 같이 날라옴 !! (연관된 엔디티도 한방 쿼리 !!)
                // join fetch를 사용하게 되면, memberexam과 teamexam을 조인해서 가져올 때, teamExam은 프록시가 아닌 '실제 엔디티'가 담겼다 !!
            List<MemberExam> resultList2 = em.createQuery(fetchJoinQuery, MemberExam.class).getResultList();

            for (MemberExam memberExam : resultList2) {
                System.out.println("member : " + memberExam.getUsername()
                        + ", " + memberExam.getTeamExam().getName());
            }

            System.out.println("컬렉션 패치 조인 :::::::::::::: -- 3번");
            String collectionFetchJoinQuery = "select t from TeamExam t join fetch t.members";
            // -- t.members -> List<MemberExam> -> Collection fetch join

            List<TeamExam> resultList3 = em.createQuery(collectionFetchJoinQuery, TeamExam.class).getResultList();

            for (TeamExam teamExam : resultList3) {
                System.out.println("teamExam : " + teamExam.getName() + " | " + teamExam.getMembers().size());
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
