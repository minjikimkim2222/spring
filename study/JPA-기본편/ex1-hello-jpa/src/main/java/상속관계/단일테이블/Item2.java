package 상속관계.단일테이블;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // -- '싱글테이블'전략으로 DB 테이블이 만들어진다 !!
@DiscriminatorColumn(name = "DTYPE") // -- DYPE 따로 추가
public class Item2 {
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
