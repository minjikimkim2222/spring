package jpabook.jpashop.web;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {
    // Book이 상속하는 Item의 공통속성

    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    // Book만의 속성
    private String author;
    private String isbn;
}
