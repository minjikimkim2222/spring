package hellojpa.OneToOne;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Locker1 {

    @Id @GeneratedValue
    private Long id;
    private String name;

    // 주테이블, 외래키 양방향
    @OneToOne(mappedBy = "locker1")
    private Member3 member3;
}
