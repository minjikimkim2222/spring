package 상속관계.MappedSuperclassExam;

import hellojpa.ManyToOne.Team1;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MemberMapped extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team1 team;

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

    public void setTeam(Team1 team) {
        this.team = team;
    }
}


