package JPQL기본;

import JPQL기본.domain.MemberExam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JPQL함수Main7 {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            em.flush();
            em.clear();

            // 1. JPQL 기본제공 함수
            // -- concat : 문자 2개 더하기 (JPQL 표준)
            String query = "select concat('a', 'b')";
            List<String> resultList1 = em.createQuery(query, String.class).getResultList();
            System.out.println("resultList1 :: " + resultList1.get(0));

            // Hibernate 구현체는 || 으로도 concat 가능하게끔 한다.
            String query2 = "select 'a' || 'b'";
            List<String> resultList2 = em.createQuery(query2, String.class).getResultList();
            System.out.println("resultList2 :: " + resultList2.get(0));

            // substring -- 부분문자열
            String query3 = "select substring(m.username, 2, 3) from MemberExam m";
            List<String> resultList3 = em.createQuery(query3, String.class).getResultList();
            System.out.println("resultList3 :: " + resultList3.get(0));

            // trim -- 문자열의 '양끝'공백을 제거해주는 함수
            // -- LTRIM : 왼쪽 공백만 제거
            // -- RTRIM : 오른쪽 공백만 제거
//            MemberExam member2 = new MemberExam();
//            member2.setUsername("      A  d  m    i     n      ");
//            em.persist(member2);

            String query4 = "select trim(m.username) from MemberExam m";
            List<String> resultList = em.createQuery(query4, String.class).getResultList();

            for (String s : resultList) {
                System.out.println("trim 테스트 ::: " + s);
            }

            // LOCATE : 원하는 문자의 시작 index를 알려주는 함수
            String query5 = "select locate('de', 'abcdeffg')";
            List<Integer> resultList4 = em.createQuery(query5, Integer.class).getResultList();

            for (Integer i : resultList4) {
                System.out.println("locate test ::: " + i);
            }

            // size : 컬렉션의 크기 반환
            String query6 = "select size(t.members) from TeamExam t";
            List<Integer> resultList5 = em.createQuery(query6, Integer.class).getResultList();
            System.out.println("size test ::: " + resultList5.get(0)); // 각 TeamExam 엔디티에 양방향매핑된 memberExam 엔디티 개수 리턴

            // index : 일반적으로 사용할수는 없고, 값타입 컬렉션에서 @OrderColumn을 사용할 때 사용할수 있는 함수
            // 값 타입 컬렉션의 위치값을 구할 때 사용가능한데.. -- 근데 그냥 안쓰는게 좋음


            // 2. 사용자 정의 함수 호출   -- 생략





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
