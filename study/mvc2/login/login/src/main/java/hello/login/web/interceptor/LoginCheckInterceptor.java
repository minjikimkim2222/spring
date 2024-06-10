package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    // preHandle -- 컨트롤러 호출전 ; handler 출력 가능
    // 서블릿 필터 (LoginCheckFilter)와 비교해보면, 코드 간결해짐
        // -- 로그인체크(인증)은, 컨트롤러 호출전에만 호출하면 되기에, preHandle만 오버라이드!
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행 : {}", requestURI);

        HttpSession session = request.getSession(false); // 있으면 세션반환, 없으면 null 반환

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
            log.info("!! 미인증 사용자 요청 !!");

            // 로그인으로 redirect
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false; // 그 다음 단계 못감!! (redirect만 실행될 뿐..)
            // -- 문제가 될 때만 false 리턴하면 됨!
        }

        return true; // (로그인 성공!) 그 다음 단계 (컨트롤러 호출 - 뷰 리턴 - postHandle - 뷰 렌더링 - afterCompletion) 쭉- 감
    }


}
