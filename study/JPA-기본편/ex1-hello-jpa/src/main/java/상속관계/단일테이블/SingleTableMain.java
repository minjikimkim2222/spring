package 상속관계.단일테이블;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class SingleTableMain {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            Movie2 movie2 = new Movie2();
            movie2.setDirector("aaa");
            movie2.setActor("bbb");
            movie2.setName("바람과 함께 사라지다"); // 상속받은 부모객체 Item1의 필드 사용 !!
            movie2.setPrice(10000); // 역시 마찬가지다

            em.persist(movie2);

            em.flush(); // 영속성 컨텍스트에 있는 걸 전부 DB에 날림
            em.clear(); // 영속성 컨텍스트 제거 -- 1차 캐시 암것도 안 남음

            // entityManager 에 추가한 다음 커밋 !! (정상적일때)
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

