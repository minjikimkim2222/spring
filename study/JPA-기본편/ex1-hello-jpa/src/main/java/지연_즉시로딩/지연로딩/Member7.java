package 지연_즉시로딩.지연로딩;

import jakarta.persistence.*;

@Entity
public class Member7 {
    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY) // -- 지연로딩 설정
    @JoinColumn(name = "TEAM_ID")
    private Team7 team7;

    // getters and setters


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

    public Team7 getTeam() {
        return team7;
    }

    public void setTeam(Team7 team7) {
        this.team7 = team7;
    }
}
