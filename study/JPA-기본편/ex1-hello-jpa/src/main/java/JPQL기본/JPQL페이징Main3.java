package JPQL기본;

import JPQL기본.domain.MemberExam;
import jakarta.persistence.*;

import java.util.List;

public class JPQL페이징Main3 {
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
//            for (int i = 0; i < 100 ; i++){
//                MemberExam member = new MemberExam();
//                member.setUsername("member" + i);
//                member.setAge(i);
//                em.persist(member);
//            }

            em.flush();
            em.clear();

            List<MemberExam> resultList= em.createQuery("select m from MemberExam m order by m.age desc", MemberExam.class)
                    .setFirstResult(1) // -- 조회시작위치 (0부터 시작)
                    .setMaxResults(10) // -- 조회할 데이터 개수
                    .getResultList();

            System.out.println("result -- size : " + resultList.size());
            for (MemberExam memberExam : resultList) {
                System.out.println("memberExam : " + memberExam);
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
