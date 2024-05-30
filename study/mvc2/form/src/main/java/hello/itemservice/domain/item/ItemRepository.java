package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    이 코드는 메모리에 아이템을 저장하고 관리하는 역할
 */
@Repository // 스프링에게 DAO 계층임을 알리는
public class ItemRepository {

    // Key : Item객체의 id, Value : Item 객체
    private static final Map<Long, Item> store = new HashMap<>(); //static

    // item의 ID를 생성하는 정적변수, 초기값은 0
    private static long sequence = 0L; //static

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);

        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    // store 맵을 초기화해, 모든 저장된 아이템을 제거
    public void clearStore() {
        store.clear();
    }

}
