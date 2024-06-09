package hello.login.web.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
    세선 관리
 */
@Component
public class SessionManager {
    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>(); // 세션Id - value
        // 동시성 이슈; 동시에 여러 스레드가 sessionstore에 접근할 수 있으므로, HashMap대신 ConcurrentHashMap 사용..

    /*
        세션 생성 !!
     */

    public void createSession(Object value, HttpServletResponse response){

        // 세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);

        response.addCookie(mySessionCookie);
    }

    /*
        세션 조회 !!
     */
    public Object getSession(HttpServletRequest request){
        // 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 값(회원 정보) 조회

        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);

        if (sessionCookie == null){
            return null; // 클라이언트가 요청한 sessionId 쿠키 값이 없다
        }

        return sessionStore.get(sessionCookie.getValue());
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName){
        if (request.getCookies() == null){
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

    /*
        세션 만료 !!
     */

    public void expire(HttpServletRequest request){
        // 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 sessionId와 값 제거
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);

        if (sessionCookie != null){
            sessionStore.remove(sessionCookie.getValue());
        }
    }
}
