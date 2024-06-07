package hello.itemservice.web.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/item")
public class ValidationItemApiController {

    // @RequestBody를 보고, JSON 형태 -> 객체(ItemSaveForm)으로 변환해주어야, 그 다음에 @Validated로 빈 검증 적용해주는데..
    // 만일 form의 price를 문자 "A"로 넘기면 타입 에러 발생!!
    // 타입 에러가 발생하면, @Validated 적용 전에, JSON -> 객체로 바꾸는 것 자체가 실패해서, /add 컨트롤러를 실행도 못해보고 에러나서 끝남!
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form,
                          BindingResult bindingResult){
        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()){
            log.info("검증 오류 발생 !! errors : {}", bindingResult);
            return bindingResult.getAllErrors(); // fieldError와 objectError 모두 반환....
        }

        log.info("성공 로직 실행 !!");
        return form;
    }
}
