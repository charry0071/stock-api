package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Builder
@Data
public class Stock {

    private BigInteger time;

    private Integer date;

    private Double price;

    private Double open;

    private Double yclose;

    private Double high;

    private Double low;

    private BigInteger vol;

    private BigInteger amount;

    private Double amplitude;

    private Double increase;

    private Double exchangerate;

    private List<Buy> buy;

    private List<Sell> sell;

    private Week week;

    private String symbol;

    private String name;

    private Double volratio;

    private List<Deal> deal;

    private List<Minute> minute;

}
