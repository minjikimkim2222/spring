package 상속관계.조인;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;

@Entity
@DiscriminatorValue("M")
public class Movie1 extends Item1{ // Item1 상속받음
    private String director;
    private String actor;

    // getter, setter

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
