package 상속관계.조인;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // -- 조인전략으로 DB 테이블이 만들어진다 !!
@DiscriminatorColumn(name = "DTYPE") // -- DYPE 따로 추가
public class Item1 {
    @Id @GeneratedValue
    private Long id;

    private String name; // 상품명

    private int price; // 상품가격

    // getter, setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
