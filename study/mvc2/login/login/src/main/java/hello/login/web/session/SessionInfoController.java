package hello.login.web.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session == null){
            return "세션이 없습니다.";
        }

        // 세선 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name : {}, value={}",
                        name, session.getAttribute(name)));

        // 세션 ID
        log.info("sessionId = {}", session.getId());

        // 세션 유효시간 -- 1800초 (30분)
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());

        // 세션 생성일시
        log.info("creationTime={}", new Date(session.getCreationTime()));

        // 세션과 연결된 사용자가, 최근에 접속한 시간; 클라이언트엥서 서버로 sessionId(JSESSIONID)를 요청한 경우 갱신
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));

        // 새로 생성된 세션인지, 아니면 과거에 만들어졌고 클라이언트가 서버로 요청해서 조회된 세션인지 여부
        log.info("isNew={}", session.isNew());

        return "세션 출력";
    }
}
