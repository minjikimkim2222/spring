package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        try {
            if (ex instanceof UserException){
                log.info("UserException resolver to 400");

                // HTTP accept header가 JSON인 경우, 아닌 경우 -- 나눠서 처리
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 상태코드로 해당 에러 정의해줄 것

                if ("application/json".equals(acceptHeader)){
                    Map<String, Object> errorResult = new HashMap<>();

                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);

                    // response 에다가 앞서 설정한 errorResult 값 넣어주어야 함
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);

                    return new ModelAndView();

                } else {
                    // TEXT/HTML
                    return new ModelAndView("error/400");
                }

            } // if (ex instanceof UserException)

        } catch (IOException e){
            log.error("resolver ex ", e);
        }

        return null;
    }
}
