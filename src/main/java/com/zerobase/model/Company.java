package com.zerobase.model;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {
    private String ticker;
    private String name;
}
