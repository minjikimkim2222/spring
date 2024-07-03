package hellojpa.ManyToMany.origin;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member4 {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @ManyToMany
    @JoinTable(name = "MEMBER_PRODUCT")
    private List<Product1> product1s = new ArrayList<>();
    private String username;
}
