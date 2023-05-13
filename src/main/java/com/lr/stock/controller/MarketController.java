package com.lr.stock.controller;

import cn.hutool.core.collection.CollUtil;
import com.lr.stock.entity.DayKlineRs;
import com.lr.stock.entity.FiveKlineRs;
import com.lr.stock.entity.Krs;
import com.lr.stock.manager.KlineManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MarketController {

    @PostMapping("/API/KLine3")
    public DayKlineRs minuteKline(@RequestParam("symbol") String symbol){
        if(symbol.contains("usa")){
            symbol = symbol.replaceAll("usa","us");
        }
        return KlineManager.minuteKlineData(symbol);
    }

    @PostMapping("/API/KLine2")
    public DayKlineRs dayKline(@RequestParam("symbol") String symbol){
        if(symbol.contains("usa")){
            symbol = symbol.replaceAll("usa","us");
        }
        return KlineManager.dayKlineData(symbol);
    }

    @PostMapping("/API/StockMinuteData")
    public FiveKlineRs fiveKline(@RequestParam("symbol") String symbol){
        //美股替换前端标识
        if(symbol.contains("usa")){
            symbol = symbol.replaceAll("usa","us");
        }
        return KlineManager.fiveKlineData(symbol);
    }

    @PostMapping("/API/Stock")
    public Krs stockKline(@RequestParam("field[]") String[] field, @RequestParam("symbol[]") String[] symbol){
        List<String> fieldLists = Arrays.asList(field);
        List<String> symbolLists = Arrays.asList(symbol);
        if(CollUtil.isEmpty(symbolLists)){
            return null;
        }
        String symbolVal = symbolLists.get(0);

        //美股替换前端标识
        if(symbolVal.contains("usa")){
            symbolVal = symbolVal.replaceAll("usa","us");
        }

        if(fieldLists.contains("volratio")){
            //行情16
            return KlineManager.stockMarket16(symbolVal);
        } else if(fieldLists.contains("exchangerate") && fieldLists.contains("amplitude")){
            //行情17
            return KlineManager.stockMarket17(symbolVal);
        } else if(fieldLists.contains("deal")){
            //行情11
            return KlineManager.stockMarket11(symbolVal);
        } else if(fieldLists.contains("minute") && fieldLists.contains("minutecount")){
            //行情13
            return KlineManager.stockMarket13(symbolVal);
        } else {
            return null;
        }

    }


}
