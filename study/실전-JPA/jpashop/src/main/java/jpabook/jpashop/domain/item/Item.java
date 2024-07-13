package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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

    // == 비즈니스 로직 == /
    /*
        stock 증가, 감소 -- setter로 값 세팅하는 것보다는, 핵심도메인에 비즈니스 로직을 추가하는 것 !
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;

        if (restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }



}
