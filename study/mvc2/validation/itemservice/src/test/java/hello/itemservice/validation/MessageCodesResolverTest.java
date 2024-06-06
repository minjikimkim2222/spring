package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){ // 객체 오류 - 메세지 코드 2개 생성 (p.26)
        String[] messageCodes =
                codesResolver.resolveMessageCodes("required", "item");

        for (String message : messageCodes){
            System.out.println("message : >>>>> " + message);
        }

        Assertions.assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverField(){ // 필드 오류 - 메세지 코드 4개 생성
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item",
                "itemName", String.class);

        for (String messageCode : messageCodes){
            System.out.println("messageCode : >>>> " + messageCode);
        }

        /*
            ValidationItemControllerV2의 addItemV4 메서드에서, rejectValue나 reject 함수에서,
            bindingResult.rejectValue("itemName", "required"); 만 넘겨줘도..

            rejectValue 메서드 안에서, MessageCodesResolver 인터페이스를 호출한다!!
            그리고 해당 messageCodes(4개)를 만들어줌.. (왜 2개 만드냐면, 필드 에러는 4개, 객체 에러는 2개의 메세지 코드를 만들도록 되어있어서)

            new FieldError("item", "itemName", null, false, messageCodes, null, null);
                즉, String[] codes를 넣는 5번째 argument에, messageCodes를 넘기는 것 !!

         */

        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
