package 값타입.값타입컬렉션.실무VER;

import jakarta.persistence.*;
import 값타입.임베디드타입.Address;

@Entity
@Table(name = "ADDRESS")
public class AddressEntity { // 엔디티 타입
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address; // 값 타입
}
