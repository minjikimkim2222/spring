package 상속관계.조인;

import jakarta.persistence.Entity;

@Entity
public class Album1 extends Item1{ // Items1 상속받음
    private String artist;
}
