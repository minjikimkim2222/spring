package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static hello.login.web.SessionConst.LOGIN_MEMBER;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;

    // 로그인 기능 1 - 쿠키로 사용자 정보 인증 .. but 보안문제 O
    @GetMapping("/login") // 모델에 loginForm 추가 -- 로그인 폼 요청
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm){
        return "login/loginForm";
    }

    //@PostMapping("/login") // 실제 로그인 기능
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult, HttpServletResponse response){
        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            // 아이디, 비밀번호 불일치는 fieldError 아니고 ObjectError이기 때문에, reject 함수 사용
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm"; // 검증 에러 실패시, 다시 로그인 뷰 요청..
        }

        // 로그인 성공 처리
            // 로그인id/로그인pw도 맞았으니, 서버에서, 쿠키를 만들어서 클라이언트에게 전달해줘야 함!!

            // 쿠키에 시간정보를 주지 않으면, 세션 쿠키 (브라우저 종료 시 모두 종료)
        Cookie idCookie = new Cookie("memberId",
                String.valueOf(loginMember.getId()));

        response.addCookie(idCookie);

        return "redirect:/";
    }

    // 로그아웃 기능1 -- 쿠키를 날린다 (쿠키의 시간을 지워버린다)
        // 로그인 시, 계속 쿠키값을 유지했던 "memberId"의 쿠키의 setMaxAge를 0으로 만들어버린다.
    //@PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName){
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    // 로그인/로그아웃 v2 - 직접 만든 세션 적용
    //@PostMapping("/login")
    public String loginV2(@Validated @ModelAttribute(name= "loginForm") LoginForm form,
                          BindingResult bindingResult, HttpServletResponse response){

        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 - 이부분 업데이트 -- 쿠키 대신 세션 적용 !!!
        sessionManager.createSession(loginMember, response);
        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";
    }

    // 로그인/로그아웃 - 직접 만든 세션 말고, 서블릿이 제공해주는 HttpSession 활용
    //@PostMapping("/login")
    public String loginV3(@Validated @ModelAttribute(name= "loginForm") LoginForm form,
                          BindingResult bindingResult, HttpServletRequest request){

        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 - 이부분 업데이트 -- 직접 만든 세션 대신 HttpSession 적용 !!

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성 (디폴트값 true)
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request){
        // 세션을 삭제한다.
        HttpSession session = request.getSession(false);
            // 세션이 있으면 기존 세션 반환, ** 없으면 null 반환 ** (false)
        if (session != null){
            session.invalidate(); // 세션 제거
        }
        return "redirect:/";
    }

    // V4 - Filter 추가 -- 로그인 성공하면, 처음 요청한 URL로 이동하도록 !!
    @PostMapping("/login")
    public String loginV4(@Validated @ModelAttribute(name= "loginForm") LoginForm form,
                          BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest request){

        if (bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성 (디폴트값 true)
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        /// ---- 이 부분 업데이트! -- redirectURL 적용
        return "redirect:" + redirectURL;
    }
}
