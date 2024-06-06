package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }
    // supports = 해당 검증기를 지원하는 여부 확인
    // item === clazz (item클래스, 검증 가능)
    // item === subItem (item의 자식 클래스일 때도, 검증 가능) - isAssignableFrom

    // 컨트롤러의 addItemV6 메서드에서 @Validated로 Item item이 clazz로 넘어왔는데,
    // return값이 결론적으로 true(Item.class === Item.class)니까, validate 함수가 실행됨 !!!
    @Override
    public void validate(Object target, Errors errors) { // 검증 대상 객체, BindingResult
        Item item = (Item) target;

        // 컨트롤러에 만들어두었던, 검증 로직 복사

        if (!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 10000) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
