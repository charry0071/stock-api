package com.lr.stock.utils;

import cn.hutool.http.Header;
import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 新浪股票接口
 */
public class SignApi {

    private static String apiUrl = "http://hq.sinajs.cn/list=";

    /**
     * 查询行情
     * @param symbol
     * @return
     */
    public static String findSymbol(String symbol){

        List<String> symbolInfo =
                Splitter.on(".").trimResults().splitToList(symbol);

        String sym = symbolInfo.get(1) + symbolInfo.get(0);
        if("us".equals(symbolInfo.get(1))){
            sym = "gb_" + symbolInfo.get(0);
        }

        String uri = (apiUrl + sym).toLowerCase(Locale.ROOT);
        String body = HttpClientRequest.doGet(uri);

        return body.substring(body.indexOf("=") + 2);
    }


}
