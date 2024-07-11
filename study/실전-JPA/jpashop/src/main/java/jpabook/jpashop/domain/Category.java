package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
        joinColumns = @JoinColumn(name = "category_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 여러 자식 카테고리가, 하나의 부모 카테고리를 가집니다 !!
    // '자식' 입장에서 '부모' 매핑

    @OneToMany(mappedBy = "parent")
    private List<Category> childs = new ArrayList<>(); // 자식은 하나의 부모 안에 여러개 속합니다
    // '부모' 입장에서, '자식' 매핑

    // == 연관관계 메서드 == (양방향 이네욤)
    public void addChildCategory(Category child){
        childs.add(child);
        child.setParent(this);
    }
}
