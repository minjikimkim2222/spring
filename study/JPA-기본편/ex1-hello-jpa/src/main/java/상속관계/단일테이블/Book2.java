package 상속관계.단일테이블;

import jakarta.persistence.Entity;

@Entity
public class Book2 extends Item2 { // Item1 상속받음
    private String author;
    private String isbn;
}
