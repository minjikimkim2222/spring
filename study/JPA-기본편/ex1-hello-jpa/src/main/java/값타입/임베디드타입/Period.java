package 값타입.임베디드타입;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 기본생성자 필수
    public Period() {
    }
}
