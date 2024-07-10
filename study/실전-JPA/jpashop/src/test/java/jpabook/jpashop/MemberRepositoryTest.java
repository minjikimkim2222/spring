package jpabook.jpashop;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 테스트케이스는, DB에 넣은 데이터값을 롤백해줍니다
@RunWith(SpringRunner.class) // -- JUnit한테, 나 Spring과 관련된 테스트를 할거야!! 라고 알려줌
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional // 엔디티매니저를 통한 모든 데이터 변경은, 항상 트랜잭션 안에서 이뤄져야 함 !!
    @Rollback(value = false)
    public void testMethodNameHere() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
        // equals 해시코드 구현한 바가 없음 -- JPA 엔디티 동일성 보장
        // 같은 영속성 컨텍스트(같은 트랜잭션 안에서) id값이 같으면, 같은 엔디티로 식별

    }

}