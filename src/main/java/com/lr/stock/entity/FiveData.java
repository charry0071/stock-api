package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FiveData {

    private Integer date;
    private Double yclose;
    private Double close;
    private List<List<Object>> minute;

}
