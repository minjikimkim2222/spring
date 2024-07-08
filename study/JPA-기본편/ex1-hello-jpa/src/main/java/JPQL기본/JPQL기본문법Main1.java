package JPQL기본;

import JPQL기본.domain.MemberExam;
import jakarta.persistence.*;

import java.util.List;

public class JPQL기본문법Main1 {
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
//            MemberExam member = new MemberExam();
//            member.setUsername("member1");
//            member.setAge(10);
//            em.persist(member);

            // 1. TypeQuery, Query [반환타입이 명확할 때 / 명확하지 않을 때 사용..]
            // -- 참고로, from 절에는 테이블명이 아닌, 엔디티명을 작성해주어야 함 !
            TypedQuery<MemberExam> typedQuery = em.createQuery("select m from MemberExam m",
                    MemberExam.class); // 꼭 반환타입을 명시해주세요!
            TypedQuery<String> typedQuery2 = em.createQuery("select m.username from MemberExam m",
                    String.class);

            Query query = em.createQuery("select m.username, m.age from MemberExam m");

            // 2. 결과조회 API -- query.getResultList(), query.getSingleResult()
            List<MemberExam> resultList = typedQuery.getResultList();
            for (MemberExam memberExam : resultList) {
                System.out.println(memberExam.getId());
            }

            String singleResult = typedQuery2.getSingleResult();
            System.out.println("singleResult : " + singleResult);

            // 3. 파라미터 바인딩 -- 이름기준, 위치기준
            // -- 이름기준
            MemberExam singleResult2 = em.createQuery("select m from MemberExam m where m.username = :username", MemberExam.class)
                .setParameter("username", "member1")
                .getSingleResult();

            System.out.println("파라미터 바인딩 : " + singleResult2.getUsername());

            // -- 위치기준 -> 웬만하면 쓰지 마세요!! -- 중간에 하나 끼워지면 버그발생하니깐


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
