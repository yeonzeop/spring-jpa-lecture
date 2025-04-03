package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id") // 테이블에는 member_id로 표기
    private Long id;

    private String name;

    @Embedded
    private Address address; // city, street, zipcode 가 속성

    @OneToMany(mappedBy = "member") // 이건 읽기 전용이며 Order 클래스의 member가 주인에요. 여기서는 수정이 불가해요
    private List<Order> orders = new ArrayList<>();

}
