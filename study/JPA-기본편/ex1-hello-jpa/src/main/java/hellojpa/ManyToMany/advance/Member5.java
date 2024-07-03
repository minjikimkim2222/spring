package hellojpa.ManyToMany.advance;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member5 {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @OneToMany(mappedBy = "member5") // -- 가짜 매핑, 연관관계 주인이 아니여요
    //@JoinTable(name = "MEMBER_PRODUCT")
    // OneToMany로 수정, List도 중간에디티로 수정 !!
    private List<MemberProduct> memberProducts = new ArrayList<>();
    private String username;
}
