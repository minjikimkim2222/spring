package JPQL기본.domain;

import jakarta.persistence.*;

@Entity
public class MemberExam {
    @Id @GeneratedValue
    private Long id;
    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일은 지연로딩 !!
    @JoinColumn(name = "TEAM_ID")
    private TeamExam teamExam;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    // 정석적으로!! 연관관계 편의 메서드 !!
    public void changeTeam(TeamExam team){
        this.teamExam = team;
        team.getMembers().add(this);
    }
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setTeamExam(TeamExam teamExam) {
        this.teamExam = teamExam;
    }

    // toString

    @Override
    public String toString() {
        return "MemberExam{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
              //  ", teamExam=" + teamExam + --> toString 만들 때, 양방향되면 큰일남!! 스택오버플로우 조심
                '}';
    }
}
