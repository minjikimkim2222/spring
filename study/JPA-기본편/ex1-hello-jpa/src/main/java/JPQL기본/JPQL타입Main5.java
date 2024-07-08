package JPQL기본;

import JPQL기본.domain.MemberExam;
import JPQL기본.domain.MemberType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JPQL타입Main5 {
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

            // 문자타입, Boolean 타입 테스트
            String query = "select m.username, 'HELLO', TRUE from MemberExam m";
            List<Object[]> resultList = em.createQuery(query).getResultList();

            System.out.println("member - username : " + resultList.get(0)[0]);
            System.out.println("HELLO : " + resultList.get(0)[1]);
            System.out.println("TRUE : " + resultList.get(0)[2]);

            // ENUM 타입을 JPQL에서 쓸 때는, 패키지명까지 포함시켜야 합니다.
            String query2 = "select m from MemberExam m" +
                        " where m.memberType = JPQL기본.domain.MemberType.ADMIN";
            em.createQuery(query2, MemberExam.class).getResultList();

            // 근데, 실제로는 파라미터 바인딩을 하니까, 복잡하지는 않음
            System.out.println("파라미터 바인딩 !! ");
            String query3 = "select m from MemberExam m" +
                    " where m.memberType = :type";
            em.createQuery(query3, MemberExam.class)
                    .setParameter("type", MemberType.ADMIN)
                    .getResultList();


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
