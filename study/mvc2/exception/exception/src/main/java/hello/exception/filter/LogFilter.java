package hello.exception.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("!! log filter init !!");
    }

    // 이전에 배웠던 필터 예제에서, getDispathcerType을 추가해준 것만 다름
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        try {
            log.info("doFilter -- REQUEST [{}][{}][{}] : ", uuid,
                    request.getDispatcherType(),
                    requestURI);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            throw e;
        } finally {
            log.info("doFilter -- RESPONSE [{}][{}][{}] : ", uuid,
                    request.getDispatcherType(),
                    requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("!! log filter destroy !!");
    }
}
