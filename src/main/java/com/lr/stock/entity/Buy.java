package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Buy {

    private Double price;

    private Integer vol;
}
