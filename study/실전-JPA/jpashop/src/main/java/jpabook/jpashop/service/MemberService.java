package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 단순 조회
@RequiredArgsConstructor // final 필드만 생성자를 만들어줌
public class MemberService {

    private final MemberRepository memberRepository;

    /*
        회원가입
     */
    @Transactional // 데이터변경은 readOnly -- false
    public Long join(Member member){
        validationDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    public void validationDuplicateMember(Member member){
        // 문제가 있으면, EXCEPTION
        List<Member> findMembers =
                memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /*
        전체 회원 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    /*
        회원 수정
     */
    @Transactional
    public void update(Long id, String name){
        Member member = memberRepository.findOne(id); // 트랜잭션이 있는 곳에서, member가 영속화된 상태구나~~

        member.setName(name); // 변경감지를 사용해서, 커밋되는 시점에 데이터 수정 !!

    }
}
