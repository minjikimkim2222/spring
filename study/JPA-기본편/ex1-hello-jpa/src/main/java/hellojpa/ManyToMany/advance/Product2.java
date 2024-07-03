package hellojpa.ManyToMany.advance;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product2 {
    @Id @GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "product2") // -- 가짜 매핑 -- 연관관계의 주인이 아니여요
    private List<MemberProduct> memberProducts; // List 자료형도 MemberProduct 엔디티로 바뀜
}
