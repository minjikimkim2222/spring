package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
    상품서비스는 상품리포지토리를 단순히 위임만 하는 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final EntityManager em;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    // 준영속 엔디티 수정방법 1 -- 변경감지 --> 결론적으로 이 방법을 더 사용해라 !!
    @Transactional
    public void updateItem(Item itemParam){ // itemParam : 파라미터로 넘어온, 준영속 상태의 엔디티
        Item findItem = em.find(Item.class, itemParam.getId()); // 같은 엔디티를 조회한다. (이때 영속성 컨텍스트에 저장되어 관리됨)

        findItem.setPrice(itemParam.getPrice()); // 여기서부터 쭉- 데이터를 수정해준다.
//        findItem.setName(itemParam.getName());

        /*
            값들이 모두 세팅되고 나면, 스프링의 @Transactional 어노테이션에 의해, 커밋이 된다.
            커밋이 되면 JPA는, flush를 날리며, 모아둔 SQL 쿼리문(insert,update,delete) 등등을 처리해준다.
         */

    }

    // 준영속 엔디티 수정방법 2 -- 병합 사용
    @Transactional
    public void update(Item itemParam){ // itemParam : 파라미터로 넘어온, 준영속 상태의 엔디티
        Item mergeItem = em.merge(itemParam); // // 병합은, 준영속 엔디티를 -> 영속 상태로 변경할 때, 사용됨

        /*
            이때 merge의 파라미터객체인 itemParam은 준영속 엔디티고,
            반환값인 mergeItem은 영속성 엔디티임.

            @Transactional로 커밋이 되면, JPA는 flush를 날리며,
            다시 변경감지기능이 동작해서, 영속성 컨텍스트에 저장되어 있던 쿼리문들을 처리해줌..
         */
    }

    /*
        영속성 컨텍스트가 자동 변경 !!
     */
    @Transactional
    public void updateItem2(Long id, String name, int price, int stockQuantity){
        Item item = itemRepository.findOne(id); // 준영속을 영속화시킨다!!

        // 영속화시켰으니, 값만 세팅해주면, 커밋시점에 JPA가 알아서 변경감지로, update해줌
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
