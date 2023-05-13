package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class Deal {

    private Integer bs;

    private Double price;

    private Integer amount;

    private Integer vol;

    private Integer time;

}
