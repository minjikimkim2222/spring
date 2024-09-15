package hello.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
    예외가 발생했을 때, API 응답으로 사용되는 객체 정의
 */
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
