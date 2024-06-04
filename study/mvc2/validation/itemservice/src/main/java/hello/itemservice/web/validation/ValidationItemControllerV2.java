package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    // addItemV1 -- 코드가 점진적으로 바뀜
    // 주의 !! BindingResult 파라미터 위치는 @ModelAttribute Item item '바로 다음'에 와야 함..
//    @PostMapping("/add")
//    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes
//    ) {
//
//        // 검증 로직
//        if (!StringUtils.hasText(item.getItemName())){
//            // 모델 item내에, itemName이 없다면..
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
//        }
//
//        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
//            bindingResult.addError(new FieldError("item", "price", "가격은 1000원 ~ 1,000,000원까지 허용합니다."));
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() >= 9999){
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
//        }
//
//        // 특정 필드가 아닌 복합 룰 검증
//        if (item.getPrice() != null && item.getQuantity() != null){
//            int resultPrice = item.getPrice() * item.getQuantity();
//
//            if (resultPrice < 10000){
//                // 특정 필드 예외가 아닌 전체 예외
//                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 : " + resultPrice));
//
//            }
//        }
//
//        // 검증에 실패하면, 모델에 실패한 에러메세지와 함께, 다시 입력 폼으로..
//        if (bindingResult.hasErrors()){
//            log.info("errors = {}", bindingResult);
//            return "validation/v2/addForm"; // 다시 입력 폼 뷰로 넘어감!!
//        }
//
//        // 여기는 검증 성공 로직
//
//        Item savedItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", savedItem.getId());
//        redirectAttributes.addAttribute("status", true);
//
//        return "redirect:/validation/v2/items/{itemId}";
//    }

    // addItemV2 - p.16 ~
    // 타입에러가 나면, 스프링이 item.getItemName()으로 기존 입력값 가져오고, bindingFailure를 true(바인딩 실패했으니까..)해주고,
    // 그리고 나서 해당 오류를 BindingResult에 스프링이 직접 추가해준다.
    // 그래서 타입에러같은 바인딩 실패에도, 사용자의 오류메세지 정상 출력 가능!
    // spring : bindingResult.addError(new FieldError("item", "itemName",
    //                    item.getItemName(), true, null, null, "상품 이름은 필수입니다."));
    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",
                    item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {

            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 10000) {
            bindingResult.addError(new FieldError("item", "quantity",
                    item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드 예외가 아닌 전체 예외
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                // 특정 필드가 아닌 전체 예외처리니까 ..
                // codes랑 arguments만 null로 넘기기
                bindingResult.addError(new ObjectError("item",
                        null, null, "가격 * 수량 의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

