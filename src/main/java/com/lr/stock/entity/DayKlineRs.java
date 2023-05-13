package com.lr.stock.entity;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class DayKlineRs {

    private List<List<Object>> data;
    private String symbol;
    private String name;
    private Integer start;
    private Integer end;
    private Integer count;
    private Integer ticket;
    private String version;
    private String message;
    private Integer code;
    private String servertime;

}
