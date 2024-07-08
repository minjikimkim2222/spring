package JPQL기본;

import JPQL기본.domain.MemberExam;
import JPQL기본.domain.MemberType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JPQL조건식Main6 {
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

            // 1. 기본 CASE 식 -- switch문과 동일
            String query =
                    "select " +
                        "case when m.age <= 10 then '학생요금'" +
                             "when m.age >= 60 then '경로요금'" +
                             "else '일반요금'" +
                        "end " +
                    "from MemberExam m";
            List<String> resultList = em.createQuery(query, String.class).getResultList();
            System.out.println("resultList : " + resultList.get(0));

            // 2. coalesce 조건식 : 하나씩 조회해서 null이 아니면 반환
            String query2 = "select coalesce(m.username, '이름없는 회원') from MemberExam m";
            List<String> resultList2 = em.createQuery(query2, String.class).getResultList();

            System.out.println("resultList2 :: " + resultList2.get(0));

            // 3. NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
            String query3 = "select NULLIF(m.username, 'memberB') from MemberExam m";
            List<String> resultList3 = em.createQuery(query3, String.class).getResultList();

            String firstElement = resultList3.get(0);

            if (firstElement == null){
                System.out.println("nullif 결과, 두 값이 같아요 !!");
            } else {
                System.out.println("nullif 결과, 두 값이 달라요 !! 이 값은 : " + firstElement);
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
