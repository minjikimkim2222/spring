package hello.core.member;

public class MemberServiceImpl implements MemberService{

    // 인터페이스는 해당 구현체를 선택해 new해줘야 한다.
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    // 회원가입
    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    // 회원조인
    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
