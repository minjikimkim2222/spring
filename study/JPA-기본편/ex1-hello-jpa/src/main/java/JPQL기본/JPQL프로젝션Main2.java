package JPQL기본;

import JPQL기본.domain.AddressExam;
import JPQL기본.domain.MemberExam;
import jakarta.persistence.*;

import java.util.List;

public class JPQL프로젝션Main2 {
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

            em.flush();
            em.clear();

            // 영속성 컨텍스트를 완전히 비우고, 쿼리문으로 반환된 엔디티 MemberExam의 변경사항이 적용이 되었기에,
            // JPQL에서 반환된 엔디티도 영속성 컨텍스트에 의해 관리된다.

            List<MemberExam> result = em.createQuery("select m from MemberExam m", MemberExam.class).getResultList();
            MemberExam findMember = result.get(0);
            findMember.setAge(20);

            // SELECT절에 조회할 대상을 다양하게 지정할 수 있다. (엔디티, 임베디드타입, 스칼라타입..)
            System.out.println("START 임베디드 타입 프로젝션 ------------");
            em.createQuery("select o.addressExam from OrderExam o", AddressExam.class).getResultList();
            System.out.println("START 스칼라 타입 프로젝션 ------------");
            em.createQuery("select distinct m.age, m.username from MemberExam m").getResultList();

            // 그런데, 위 스칼라타입프로젝션처럼, 여러 값을 조회하려면 어떻게 해야 하지 ??
                // 1. Query 타입 (반환타입이 명확하지 않을 때 )
            System.out.println("query 타입으로 조회 ------- ");
            Query query = em.createQuery("select distinct m.age, m.username from MemberExam m");

                // 2. Object[] 타입으로 조회
            System.out.println("object[] 타입으로 조회 ------");
            List<Object[]> resultList = em.createQuery("select distinct m.age, m.username from MemberExam m").
                    getResultList();

            System.out.println("resultList[0] : " + resultList.get(0)[0]);
            System.out.println("resultList[1] : " + resultList.get(0)[1]);

                // 3. new 명령어로 조회 -- 가장 간단함
            List<MemberDTO> resultList2 = em.createQuery("select new JPQL기본.MemberDTO(m.username, m.age) from MemberExam m",
                    MemberDTO.class).getResultList();

            MemberDTO memberDTO = resultList2.get(0);

            System.out.println("resultList2 -- username : " + memberDTO.getUsername());
            System.out.println("resultList2 -- age : " + memberDTO.getAge());

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
