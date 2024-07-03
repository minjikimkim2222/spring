package hellojpa.ManyToMany.advance;

import jakarta.persistence.*;

@Entity
public class MemberProduct {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID") // -- 연관관계 주인 !!!
    private Member5 member5;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID") // -- 연관관계 주인 !!
    private Product2 product2;

    // 맘대로 칼럼값 추가 가능 !! -- 엔디티로 승격했기에
    private int count;

    private int price;


}
