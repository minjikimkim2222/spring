package hello.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
    // ExceptionResolver는 resolve, 이름그대로 '해결'하는 것이 목적.
    // 즉, ExceptionResolver는 Exception을 처리해서, 마치 정상흐름처럼 보이게 하는 것이 목적

    /*
        여기서는, IllegalArgumentException이 발생하면,
            response.sendError(400)를 호출해서 HTTP 상태를 400으로 지정하고, 빈 ModelAndView 객체를 반환함.
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                 Object handler, Exception ex) { // handler : 컨트롤러 정보, ex : 컨트롤러에서 발생한 예외
        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");

                // IllegalArgumenrException에러가 터지면, 무조건 500에러를 -> 400에러로 바꿔서 에러를 보내줄거임
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

                return new ModelAndView();
            }
        } catch (IOException e) {
            log.error("resolver ex : ", e);
        }

        return null;
    }
}
