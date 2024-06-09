package hello.login.web.member;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {
    private final MemberRepository memberRepository;

    // @ModelAttribute 2가지 역할
    // 1. 요청파라미터를 모델 객체에 바인딩
    // -- 예를 들어, HTTP 요청에서 form 데이터가 전달되면, 이 데이터는 @ModelAttribute가 지정한 객체의 필드로 매핑
    // -- 주로 POST 요청에서 날라온 form 데이터를 모델 객체로 바인딩할 때..

    // 2. 모델에 객체 추가
    // -- model.addAttribute("member", new Member())
    // -- 주로, GET 요청에서 자바 객체를 모델에 추가할 때..
    @GetMapping("/add") // 회원가입 폼 요청
    public String addForm(@ModelAttribute("member") Member member){
        return "members/addMemberForm";
    }

    @PostMapping("/add") // 회원가입 유효성 검사후, 성공하면 memberRepository에 멤버가입
    public String saveMember(@Validated @ModelAttribute("member") Member member,
                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "members/addMemberForm"; // 검증 실패!
        }
        // 검증 성공
        memberRepository.save(member);
        return "redirect:/";
    }
}
