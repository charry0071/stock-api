package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class Sell {

    private Double price;

    private Integer vol;

}
