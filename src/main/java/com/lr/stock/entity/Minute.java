package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Builder
@Data
public class Minute {

    private BigInteger amount;
    private Double avprice;
    private Double high;
    private Double increase;
    private Double low;
    private Double open;
    private Double price;
    private Double risefall;
    private Integer time;
    private BigInteger vol;

}
