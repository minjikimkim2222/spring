package 영속성전이_고아객체;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("hello2");

        EntityManager em = entityManagerFactory.createEntityManager();

        // 트랜잭션 시작 필요 !!
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // code
        try {
            /*
                cascade 적용 전, main 코드
                넘 기찮군..
                parent에서 연관관계 편의메서드로 addChild만 해줘도,
                영속성 컨텍스트에 자식애들도 알아서 다 들어갔음 좋겠어..
             */
//            Parent parent = new Parent();
//            Child child1 = new Child();
//            Child child2 = new Child();
//
//            parent.addChild(child1);
//            parent.addChild(child2);
//
//            em.persist(parent);
//            em.persist(child1);
//            em.persist(child2);

            Parent parent = new Parent();
            Child child1 = new Child();
            Child child2 = new Child();

            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent); // cascade 이후 parent만 넣어도 자식애들도 같이 영속화됨
//            em.persist(child1);
//            em.persist(child2);

            // 부모객체에서 자식객체 연관관계 끊으면..
            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            findParent.getChildList().remove(0);
            // 첫번째 자식 지우면, 부모를 잃은 고아객체도 같이 삭제됨


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
