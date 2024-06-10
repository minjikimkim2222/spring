package hello.login.web.filter;

import hello.login.web.SessionConst;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    // 인증 필터를 적용해도 홈, 회원가입, 로그인 / 로그아웃, css 리소스에는 접근가능해야 함
    // 화이트 리스트 경로는, 인증과 무관하게 항상 허용된다.
    // 즉 반대로 말하자면, 화이트 리스트를 제외한 나머지 모든 경로에는, 인증 체크 로직을 적용 !
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout","/css/*"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI(); // 해당 요청에 대한 URL

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        try {
            log.info("인증 체크 필터 시작 : {}", requestURI);

            if (isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 : {}", requestURI);

                HttpSession session = httpRequest.getSession(false);

                // 세션 자체가 없거나, 세션이 있더라도 세션ID가 없다면.. 로그인 안한사람!
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
                    log.info("미인증 사용자 요청 : {}", requestURI);

                    // ** 로그인으로 redirect **
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    // 로그인 안한 사용자가, redirect되어 로그인한 이후에, 원래 보던 페이지로 돌아가게끔..
                    // 예) 상품관리화면 보려고 들어갔다가 로그인 화면으로 이동한 경우, 로그인 이후 다시 상품관리화면으로 돌아가고 싶다..

                    return; // 여기가 중요! 필터 더는 진행 X. 그렇기에 이후에 필터는 물론, 서블릿, 컨트롤러도 더는 호출 X
                }
            }

            // 로그인 성공한 사람 -- 이후, 그다음 필터나 / 서블릿 / 컨트롤러 호출할 수 있게끔..
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e){
            throw e; // 물론 예외 로깅도 가능하지만, 톰캣까지 예외를 보내주어야 함.
        } finally {
            log.info("인증 체크 필터 종료 : {}", requestURI);

        }
    }

    /*
        화이트 리스트의 경우, 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURL){
        return !PatternMatchUtils.simpleMatch(whitelist, requestURL);
        // requestURL이 whitelist에 포함되지 않아야(인증 체크 O) true 리턴
    }
}
