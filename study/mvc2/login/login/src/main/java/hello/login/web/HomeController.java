package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    // 로그인한 사용자 Id를 Cookie를 이용해서 보여줌
        // required=false값을 줘서, 로그인 안한 사용자도 들어올 수 있게끔 했다.
    //@GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId,
                            Model model){

        if (memberId == null){
            return "home"; // 로그인 쿠키가 없는 사용자는 기존 home으로 보낸다.
        }

        // 로그인한 유저가 있는지 체크
        Member loginMember = memberRepository.findById(memberId);

        if (loginMember == null){
            return "home"; // 로그인한 유저가 없어도 홈으로 보냄.
        }

        // 로그인 쿠키 있으면, 모델 member를 담아, loginHome 뷰로 보낸다.
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    // 로그인 처리2 -- 쿠키가 아닌 세션 이용한 것..
   // @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model){

        // 세션 관리자에 저장된 회원정보조회
        Member member = (Member) sessionManager.getSession(request);

        if (member == null){
            return "home"; // 클라이언트의 쿠키값에 해당하는 정보가 세션저장소에 없음..
        }

        // 로그인된 사용자 이름을 home 뷰에 띄우기 위해
        model.addAttribute("member", member);
        return "loginHome";
    }

   // @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){

        // 세션이 없으면 home
        HttpSession session = request.getSession(false);

        if (session == null){
            return "home";
        }

        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (member == null){
            return "home"; // 클라이언트의 쿠키값에 해당하는 정보가 세션저장소에 없음..
        }

        // 로그인된 사용자 이름을 home 뷰에 띄우기 위해
        model.addAttribute("member", member);
        return "loginHome";
    }

    // 스프링이 제공해주는 @SessionAttribute
        // -- 세션을 생성해주지는 않고, LoginController에서 생성해준 세션값을 쉽게 가져오는..
    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model){

        // 세션에 회원데이터가 없으면 home
        if (loginMember == null){
            return "home";
        }

        // 로그인된 사용자 이름을 home 뷰에 띄우기 위해
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}