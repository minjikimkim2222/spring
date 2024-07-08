package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Category extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Category parent; // 상위 카테고리 -- 셀프 매핑

    // 양방향으로 Child list까지..
    @OneToMany(mappedBy = "parent")
    private List<Category> childs = new ArrayList<>(); // 자식 카테고리들

    // CATEGORY는 ITEM과 다대다 관계
    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM",
        joinColumns = @JoinColumn(name = "CATEGORY_ID"),  // 현재 엔디티의 FK
        inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    ) // 반대편 엔디티의 FK
    private List<Item> items = new ArrayList<>();
}
