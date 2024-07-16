package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("B")
public class Book extends Item{
    private String author;
    private String isbn;

    // 생성 메서드
    public static Book createBook(String name, int price, int stockQuantity){
        Book book = new Book();

        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);

        return book;
    }
}
