package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class Week {

    private Double week1;

    private Double week4;

    private Double week13;

    private Double week26;

    private Double week52;

}
