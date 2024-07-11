package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

/*
    값 타입 -- 변경불가능하게 설계해야 한다!!

    -- setter는 제거하고, 생성자에서 값을 모두 초기화해서 변경불가능하게 만들어요 !!
 */
@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address(){
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
