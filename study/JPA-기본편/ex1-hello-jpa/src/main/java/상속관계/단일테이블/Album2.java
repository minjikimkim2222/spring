package 상속관계.단일테이블;

import jakarta.persistence.Entity;

@Entity
public class Album2 extends Item2 { // Items1 상속받음
    private String artist;
}
