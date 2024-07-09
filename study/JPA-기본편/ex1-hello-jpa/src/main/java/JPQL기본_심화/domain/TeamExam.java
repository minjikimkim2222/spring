package JPQL기본_심화.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TeamExam {
    @Id @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "teamExam")
    @BatchSize(size = 100)
    private List<MemberExam> members = new ArrayList<>();

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

    public List<MemberExam> getMembers() {
        return members;
    }

    public void setMembers(List<MemberExam> members) {
        this.members = members;
    }
}
