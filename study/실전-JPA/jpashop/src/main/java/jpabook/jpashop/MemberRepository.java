package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
@Repository
public class MemberRepository {

    // JPA이기 때문에, 엔디티 매니저가 필요합니다!!
    @PersistenceContext
    private EntityManager em; // 엔디티매니저 주입

    public Long save(Member member){
        em.persist(member);

        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

}
