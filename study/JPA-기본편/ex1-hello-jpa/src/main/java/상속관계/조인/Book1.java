package 상속관계.조인;

import jakarta.persistence.Entity;

@Entity
public class Book1 extends Item1{ // Item1 상속받음
    private String author;
    private String isbn;
}
