package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * K线数据总返回结果
 */
@Builder
@Data
public class Krs {

    private List<Stock> stock;

    private Integer start;

    private Integer end;

    private Integer count;

    private Integer ticket;

    private String version;

    private String message;

    private Integer code;

    private String servertime;




}
