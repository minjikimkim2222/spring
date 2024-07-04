package 상속관계.MappedSuperclassExam;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import 상속관계.단일테이블.Movie2;

import java.time.LocalDateTime;

public class MappedSuperClassMain {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            MemberMapped member = new MemberMapped();
            member.setUsername("minji");
            member.setCreatedBy("kim");
            member.setCreatedDate(LocalDateTime.now());

            // entityManager 에 추가한 다음 커밋 !! (정상적일때)
            tx.commit();
        } catch (Exception e) {
            // 문제가 있으면, rollback
            tx.rollback();
        } finally {
            em.close();
        }
        entityManagerFactory.close();
    }
}
