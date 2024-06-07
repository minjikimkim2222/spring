package hello.login;

import hello.login.domain.item.Item;
import hello.login.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final ItemRepository itemRepository;

    // 테스트용 데이터 넣기
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
