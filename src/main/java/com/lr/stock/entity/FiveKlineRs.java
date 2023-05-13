package com.lr.stock.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FiveKlineRs {

    private Integer code;

    private String symbol;

    private String name;

    private List<FiveData> data;

    private Integer ticket;

    private String version;

    private Object message;

    private String servertime;

}
