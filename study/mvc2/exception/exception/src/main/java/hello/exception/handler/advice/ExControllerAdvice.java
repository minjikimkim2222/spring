package hello.exception.handler.advice;

import hello.exception.exception.UserException;
import hello.exception.handler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {
    // 1. 리턴값 -- 그저 자바 객체 (ErrorResult)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException ex){
        log.error("[exceptionHandler] ex", ex);

        return new ErrorResult("BAD", ex.getMessage());
    }

    // 2. 리턴값 - ResponseEntity
    // 파라미터에 UserException 있으면, @ExceptionHandler에 생략해도 됨. 근데 난 헷갈리니깐 쓸랭
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException ex){
        log.error("[exceptionHandler] ex", ex);

        ErrorResult errorResult = new ErrorResult("USER-EX", ex.getMessage());

        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    // 실수로 놓친 예외들이, 여기서 공통으로 처리된다
    // @ExceptionHandler는 지정된 예외 클래스의, 자식 클래스까지 처리해주니깐
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e){
        log.error("[exceptionHandler] ex", e);

        return new ErrorResult("EX", "내부 오류");
    }

}
