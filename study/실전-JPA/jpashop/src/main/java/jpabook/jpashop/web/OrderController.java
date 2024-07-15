package jpabook.jpashop.web;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    // 상품 주문
    @GetMapping(value = "order")
    public String createForm(Model model){
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "order")
    public String create(@RequestParam("memberId") Long memberId,
                         @RequestParam("itemId") Long itemId,
                         @RequestParam("count") int count){ // 폼데이터를 받아올 때는, RequestParam을 쓸 수 있었자나!! (크롬 개발자도구 -- payload 개좋아요)

        orderService.order(memberId, itemId, count);
        return "redirect:/orders"; // 주문이 끝나면, 상품 주문 내역이 있는 /orders URL로 리다이렉트 !!
    }

    /*
        주문 목록 검색
     */
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        // @ModelAttribute로 OrderSearch도 자동으로 모델에 담기고, 뷰로부터 폼데이터를 받아올 수 있음 !!

        List<Order> orders = orderService.findOrders(orderSearch); // 검색조건(OrderSearch)에 맞는 orders 반환
        model.addAttribute("orders", orders);
            // 여기서 검색조건이 없어도, 현재 주문된 애들이 화면에 뜰 수 있는 이유는, findOrders에서 findAllByCriteria에서 동적쿼리로,
            // 검색조건이 없으면 그냥 select하게 구현해놓았기 때문 !!

        return "order/orderList";
    }

    /*
        주문 취소
        /orders/" + id + "/cancel
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){

        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }
}
