package hello.login.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    // preHandle -- 컨트롤러 호출 이전 (핸들러 어댑터 호출 이전)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);
        /*
            서블릿 필터인 경우 지역변수로 해결가능하지만, 스프링 인터셉터는 호출시점이 완전히 분리되어 있다.
            따라서 preHandle에서 지정한 값을 postHandle, afterCompletion에서도 같이 쓰려면 "request"에 담아두어야 한다.
            (LogInterceptor도 싱글톤처럼 사용되기에, 멤버변수 사용하면 위험함..)
         */

        // @RequestMapping : HandlerMethod
        // 정적 리소스 : ResourceHttpRequestHanlder
        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보가 포함됨
        }

        log.info("preHandler -- REQUEST [{}][{}][{}] : ", uuid, requestURI, handler); // 여기선, 어떤 컨트롤러가 호출되는지도 앎

        return true; // false면, 이 단계에서 끝남. 더이상 진행 X
    }

    // postHandle - 컨트롤러 호출 이후 (단, 예외 발생시 호출되지 않고 이 이후 모두 중단됨.)
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        // 얘는 특이하게 ModelAndView를 반환해줌 (preHandle은 handler를, afterCompletion은 ex를..)
        log.info("postHandler [{}] : ", modelAndView);
    }

    // afterCompletion - 뷰 렌더링 이후에 호출 (예외가 발생해도 항상 호출됨. 이 경우, 예외 ex를 파라미터로 받아, 로그 출력도 가능..)
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);

        log.info("afterCompletion -- RESPONSE [{}][{}] : ", logId, requestURI);

        // afterCompletion에서 특이하게 제공해주는 exception 출력 !
        if (ex != null){ // 에러 없으면 null이니깐..
            log.error("afterCompletion error !! : ", ex );
        }
    }
}
