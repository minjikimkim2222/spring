package 값타입.값타입컬렉션.실무VER;


import jakarta.persistence.*;
import 값타입.임베디드타입.Address;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member10 {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;
    @Embedded
    private Address homeAddress;


    // 값 타입 컬렉션 ->> 실무에서는 이와 같이, 엔디티를 만들어, 일대다 관계를 고려 !!
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID") // 특수한 경우라, JoinColumn을 One에다가..!
    private List<AddressEntity> addressHistory = new ArrayList<>();
}
