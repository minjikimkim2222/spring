package 값타입.임베디드타입;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Member8 {
    @Id @GeneratedValue
    private Long id;
    private String name;

    @Embedded
    private Period workPeriod;

    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",
                column = @Column(name = "work_city")),
            @AttributeOverride(name = "street",
                    column = @Column(name = "work_street")),
            @AttributeOverride(name = "zipcode",
                    column = @Column(name = "work_zipcode"))
    })
    private Address workAddress;

}
