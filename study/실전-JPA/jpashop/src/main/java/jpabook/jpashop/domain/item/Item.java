package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // -- 상속 전략 -- 싱글테이블
@DiscriminatorColumn(name = "dtype") // -- 상속자손 칼럼 구분해야 하니깐 ^^
public abstract class Item { // 추상클래스 -- 상속된 자손클래스들을 위한 몸체이기 때문
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

}
