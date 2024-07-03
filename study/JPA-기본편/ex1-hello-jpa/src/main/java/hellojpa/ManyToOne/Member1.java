package hellojpa.ManyToOne;

import jakarta.persistence.*;

@Entity
public class Member1 {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;
    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team1 getTeam() {
        return team;
    }

    public void changeTeam(Team1 team) {
        this.team = team;

        // ! 연관관계 편의 메서드 !
        team.getMembers().add(this);
    }
}

