package com.zerobase.persist.entity;

import com.zerobase.model.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "COMPANY")
@Getter
@NoArgsConstructor
@ToString
/*
* @Setter 가 없는 이유는
* 데이터 베이스에서 데이터를 주고 받기위해서만 사용하기 위함
* 즉, 데이터를 변경을 위한 클래스를 하나 더 만드는 것이
* 예상치 못한 에러를 예방함
* */
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;
    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
