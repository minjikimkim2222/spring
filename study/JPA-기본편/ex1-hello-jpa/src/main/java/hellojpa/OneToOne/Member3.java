package hellojpa.OneToOne;

import jakarta.persistence.*;

@Entity
public class Member3 {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker1 locker1;
    private String username;
}
