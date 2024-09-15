package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    // 단순히 회원조회하는 기능
    // 예외테스트를 위해, URI에 전달된 id값이 ex면, 예외발생..
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id){
        if (id.equals("ex")){
            throw new RuntimeException("잘못된 사용자"); // 500 상태코드로 처리하고 싶고
        }

        if (id.equals("bad")) { // 400 상태코드로 처리하고 싶어
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if (id.equals("user-ex")){ // 사용자 예외 정의
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1(){
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2(){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad"
        , new IllegalArgumentException());
    }

    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data){
        return "ok";
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
