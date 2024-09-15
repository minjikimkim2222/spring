package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.handler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    @GetMapping("/api2/members/{id}")
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
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
