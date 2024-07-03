package hellojpa.ManyToOne;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team1 {
    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    // 이 코드 추가하면, 양방향
    // 반대쪽에서는 연관관계 주인이 아니기에, 외래키를 관리하지 않기에, 해당 테이블에 조회만 가능
    @OneToMany(mappedBy = "team")
    private List<Member1> members = new ArrayList<>();

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

    public List<Member1> getMembers() {
        return members;
    }

    public void setMembers(List<Member1> members) {
        this.members = members;
    }


}
