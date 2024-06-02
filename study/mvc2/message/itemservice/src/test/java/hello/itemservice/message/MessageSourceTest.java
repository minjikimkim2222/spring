package hello.itemservice.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
public class MessageSourceTest {
    @Autowired
    MessageSource messageSource; // resources밑에 있는 messages 파일들을 기본으로 불러들임

    @Test
    void helloMsg(){
        String result = messageSource.getMessage("hello", null, null);
        // locale은 한국인지, 영어인지.. 근데 null을 넘기니까 디폴트파일인 messages.properties 실행 !

        assertThat(result).isEqualTo("안녕");
    }

    @Test
    void notFoundMessageCode(){
        assertThatThrownBy(() -> messageSource.getMessage("no_code", null, null)
        ).isInstanceOf(NoSuchMessageException.class);
    } // 찾고자 하는 메세지(no_code.properties)가 없는 경우, NoSuchMessageException 발생..

    @Test
    void notFoundMessageCodeDefaultMessage(){
        String message = messageSource.getMessage("no_code", null, "기본 메세지", null);

        assertThat(message).isEqualTo("기본 메세지");
    } // 기본메세지를 주면, 에러가 안난다.


    @Test
    void argumentMessage(){
        String message = messageSource.getMessage("hello.name", new Object[]{"Spring !!"}, null);

        assertThat(message).isEqualTo("안녕 Spring !!");
    } // 매개변수를 사용한 테스트코드

    // 국제화 파일 선택 예제
    // 1. 기본 언어부터
    @Test
    void defaultLang(){
        assertThat(messageSource.getMessage("hello", null, null)).isEqualTo("안녕");
        assertThat(messageSource.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    }
        // 첫번째는 locale 정보가 없으므로 messages를 사용하지만,
        // 두번째는 locale 정보는 있지만, messages_ko 파일이 없어서, 디폴트인 messages 사용

    // 이번에는 영어로 돌려보자
    @Test
    void enLang(){
        String message = messageSource.getMessage("hello", null, Locale.ENGLISH);
        assertThat(message).isEqualTo("hello");

        String message2 = messageSource.getMessage("hello.name", new Object[]{"end !!"}, Locale.ENGLISH);
        assertThat(message2).isEqualTo("hello end !!");
    }

}
