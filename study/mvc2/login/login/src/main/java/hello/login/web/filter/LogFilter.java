package hello.login.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/*
    필터 흐름
    -- HTTP 요청 - WAS - 필터 - 서블릿 - 컨트롤러

    필터 제한
    -- (적절하다면) HTTP 요청 - WAS - 필터 - 서블릿 - 컨트롤러 // 로그인 사용자
    -- (적절하지 않다면) HTTP 요청 - WAS - 필더 (적절하지 않아, 서블릿 호출 X) // 비로그인 사용자

 */
@Slf4j
public class LogFilter implements Filter {

    // 필터 초기화 -- 서블릿 컨테이너가 생성될 때 호출됨
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("#######  log filter init ########");
    }

    // 고객의 요청이 올 때마다 해당 메서드 호출됨.
    // >> 모든 사용자 요청에 대해 로그를 남겨보자! <<
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        log.info("#######  log filter dofilter ########");

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpRequest.getRequestURI();

        // 요청온 것을 구분하기 위해 UUID를 남긴다. -- 요청당 임의의 uuid를 생성해둔다
        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}] : ", uuid, requestURI);

            // 중요!! 다음 필터가 있으면 필터 호출, '없으면' 서블릿 호출 !!
            // 만약 이 로직이 없다면, 다음 단계로 진행되지 않음 !
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e){
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}] : ", uuid, requestURI);
        }
    }

    // 필터 종료 메서드 -- 서블릿 컨테이너가 종료될 때 호출
    @Override
    public void destroy() {
        log.info("#######  log filter destory ########");
    }
}
