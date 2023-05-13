package com.lr.stock.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lr.stock.entity.DayKlineRs;
import com.lr.stock.entity.FiveData;
import com.lr.stock.entity.FiveKlineRs;
import com.lr.stock.entity.Minute;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 财经接口
 */
public class EastMoneyApi {

    private static Map<String, String> secidEnum = Maps.newHashMap();

    private static String apiUrl = "https://push2his.eastmoney.com";

    private static String minuteUrl = "%s/api/qt/stock/trends2/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58&secid=%s&ndays=%s&iscr=0&iscca=0";

    //日线 默认获取3年前
    private static String dayUrl = "%s/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=20200101&end=20500101&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid=%s&klt=101&fqt=0";

    private static String fzUrl = "%s/api/qt/stock/kline/get?fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61&beg=0&end=20500101&ut=fa5fd1943c7b386f172d6893dbfba10b&rtntype=6&secid=%s&klt=1&fqt=0";

    static {
        secidEnum.put("sh","1.");
        secidEnum.put("sz","0.");
        secidEnum.put("us","105.");
        secidEnum.put("hk","116.");
    }

    public static String doGet(String url){
        return HttpUtil.get(url);
    }

    /**
     * 分时数据
     * @param symbol
     * @return
     */
    public static List<Minute> getMinuteData(String symbol){

        List<Minute> rsMinute = Lists.newArrayList();

        List<String> symbolInfo =
                Splitter.on(".").trimResults().splitToList(symbol);

        String secid = secidEnum.get(symbolInfo.get(1)) + symbolInfo.get(0);
        String uri = String.format(minuteUrl,apiUrl,secid,"1");
        String klineRs = doGet(uri);

        if(!klineRs.contains("trends")){
            //判断是否美股
            if("us".equals(symbolInfo.get(1))){
                secid = "106." + symbolInfo.get(0);
                uri = String.format(minuteUrl,apiUrl,secid,"1");
                klineRs = doGet(uri);
            } else {
                System.out.println("没有获取到K线数据");
                return null;
            }
        }

        JSONObject klineJson = JSON.parseObject(klineRs);

        JSONObject kdata = klineJson.getJSONObject("data");

        String preClose = kdata.getString("preClose");

        JSONArray trends = kdata.getJSONArray("trends");

        trends.stream().forEach(trand -> {
            //分时K数据
            String[] kinfo = Convert.toStr(trand).split(",");

            Number number = NumberUtil.parseNumber(kinfo[6]);
            String amo = NumberUtil.toStr(number);
            BigInteger amount = new BigInteger(amo);

            BigDecimal avprice = NumberUtil.round(kinfo[7], 2);

            BigDecimal high = NumberUtil.round(kinfo[3], 2);

            BigDecimal low = NumberUtil.round(kinfo[2], 2);

            BigDecimal open = NumberUtil.round(kinfo[1], 2);
            BigDecimal price = open;

            //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
            BigDecimal chang_rate = null;
            if ((price).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(preClose).compareTo(new BigDecimal("0")) != 0) {
                chang_rate = (price).subtract(new BigDecimal(preClose));
                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(preClose), 2, RoundingMode.HALF_UP);
            }

            BigDecimal risefall = price.subtract(new BigDecimal(preClose));

            BigDecimal increase = chang_rate;

            String time = DateUtil.format(DateUtil.parse(kinfo[0]), "Hmm");
            if(symbol.contains("us")){
                //美股时间转早上显示
                DateTime dateTime = DateUtil.offsetHour(DateUtil.parse(kinfo[0]), 12);
                time = DateUtil.format(dateTime, "Hmm");
            }

            BigInteger vol = new BigInteger(kinfo[5]);

            Minute mute = Minute.builder()
                    .amount(amount)
                    .avprice(avprice.doubleValue())
                    .high(high.doubleValue())
                    .increase(increase.doubleValue())
                    .low(low.doubleValue())
                    .open(open.doubleValue())
                    .price(price.doubleValue())
                    .risefall(risefall.doubleValue())
                    .time(Integer.parseInt(time)).vol(vol).build();

            rsMinute.add(mute);

        });

        return rsMinute;
    }


    /**
     * 5日分时数据
     * @param symbol
     * @return
     */
    public static FiveKlineRs getFiveData(String symbol){

        List<String> symbolInfo =
                Splitter.on(".").trimResults().splitToList(symbol);

        String secid = secidEnum.get(symbolInfo.get(1)) + symbolInfo.get(0);
        String uri = String.format(minuteUrl,apiUrl,secid,"1");
        String klineRs = doGet(uri);

        if(!klineRs.contains("trends")){
            //判断是否美股
            if("us".equals(symbolInfo.get(1))){
                secid = "106." + symbolInfo.get(0);
                uri = String.format(minuteUrl,apiUrl,secid,"1");
                klineRs = doGet(uri);
            } else {
                System.out.println("没有获取到K线数据");
                return null;
            }
        }

        JSONObject klineJson = JSON.parseObject(klineRs);

        JSONObject kdata = klineJson.getJSONObject("data");

        String preClose = kdata.getString("preClose");

        String name = kdata.getString("name");

        JSONArray trends = kdata.getJSONArray("trends");

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        List<List<Object>> minute = Lists.newArrayList();

        String copySymbol = symbol;

        trends.stream().forEach(trand -> {
            //分时K数据
            String[] kinfo = Convert.toStr(trand).split(",");

            Number number = NumberUtil.parseNumber(kinfo[6]);
            String amo = NumberUtil.toStr(number);
            BigInteger amount = new BigInteger(amo);

            BigDecimal avprice = NumberUtil.round(kinfo[7], 2);

            BigDecimal high = NumberUtil.round(kinfo[3], 2);

            BigDecimal low = NumberUtil.round(kinfo[2], 2);

            BigDecimal open = NumberUtil.round(kinfo[1], 2);

            BigDecimal price = open;

            //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
            BigDecimal chang_rate = null;
            if ((price).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(preClose).compareTo(new BigDecimal("0")) != 0) {
                chang_rate = (price).subtract(new BigDecimal(preClose));
                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(preClose), 2, RoundingMode.HALF_UP);
            }

            BigDecimal risefall = price.subtract(new BigDecimal(preClose));

            BigDecimal increase = chang_rate;

            String time = DateUtil.format(DateUtil.parse(kinfo[0]), "Hmm");
            if(copySymbol.contains("us")){
                //美股时间转早上显示
                DateTime dateTime = DateUtil.offsetHour(DateUtil.parse(kinfo[0]), 12);
                time = DateUtil.format(dateTime, "Hmm");
            }

            BigInteger vol = new BigInteger(kinfo[5]);

            List<Object> stockInfo = Lists.newLinkedList();
            stockInfo.add(Convert.toInt(time));
            stockInfo.add(price);
            stockInfo.add(low);
            stockInfo.add(high);
            stockInfo.add(NumberUtil.round(kinfo[4], 2).doubleValue());
            stockInfo.add(vol);
            stockInfo.add(amount);
            stockInfo.add(avprice);
            minute.add(stockInfo);

        });

        List<FiveData> fiveData = Lists.newArrayList();
        FiveData build = FiveData.builder()
                .yclose(Double.parseDouble(preClose))
                .date(dateInt)
                .close(Double.parseDouble(preClose))
                .minute(minute)
                .build();
        fiveData.add(build);

        if(symbol.contains("us")){
            symbol = symbol + "a";
        }

        return FiveKlineRs.builder()
                .code(0)
                .data(fiveData)
                .name(name)
                .servertime(DateUtil.now())
                .symbol(symbol)
                .ticket(31)
                .version("2.0").build();
    }


    /**
     * 日 k线 数据
     * @param symbol
     * @return
     */
    public static DayKlineRs getDayKlineData(String symbol){

        List<String> symbolInfo =
                Splitter.on(".").trimResults().splitToList(symbol);

        String secid = secidEnum.get(symbolInfo.get(1)) + symbolInfo.get(0);
        String uri = String.format(dayUrl,apiUrl,secid);
        String klineRs = doGet(uri);

        if(!klineRs.contains("klines")){
            //判断是否美股
            if("us".equals(symbolInfo.get(1))){
                secid = "106." + symbolInfo.get(0);
                uri = String.format(dayUrl,apiUrl,secid);
                klineRs = doGet(uri);
            } else {
                System.out.println("没有获取到K线数据");
                return null;
            }
        }

        JSONObject klineJson = JSON.parseObject(klineRs);

        JSONObject kdata = klineJson.getJSONObject("data");

        String preClose = kdata.getString("prePrice");

        String name = kdata.getString("name");

        JSONArray trends = kdata.getJSONArray("klines");

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        List<List<Object>> dayKlineDatas = Lists.newArrayList();

        for(int i=0;i<trends.size();i++){
            Object trand = trends.get(i);
            //分时K数据
            String[] kinfo = Convert.toStr(trand).split(",");

            Number number = NumberUtil.parseNumber(kinfo[6]);
            String amo = NumberUtil.toStr(number);
            BigInteger amount = new BigInteger(amo);

            BigDecimal avprice = NumberUtil.round(kinfo[7], 2);

            BigDecimal high = NumberUtil.round(kinfo[3], 2);

            BigDecimal low = NumberUtil.round(kinfo[2], 2);

            BigDecimal open = NumberUtil.round(kinfo[1], 2);

            BigDecimal price = open;

            //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
            BigDecimal chang_rate = null;
            if ((price).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(preClose).compareTo(new BigDecimal("0")) != 0) {
                chang_rate = (price).subtract(new BigDecimal(preClose));
                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(preClose), 2, RoundingMode.HALF_UP);
            }

            BigDecimal risefall = price.subtract(new BigDecimal(preClose));

            BigDecimal increase = chang_rate;


            String time = DateUtil.format(DateUtil.parse(kinfo[0]), "yyyyMMdd");
            if(symbol.contains("us")){
                //美股时间转早上显示
                DateTime dateTime = DateUtil.offsetHour(DateUtil.parse(kinfo[0]), 12);
                time = DateUtil.format(dateTime, "yyyyMMdd");
            }

            BigInteger vol = new BigInteger(kinfo[5]);

            List<Object> stockInfo = Lists.newLinkedList();
            stockInfo.add(Convert.toInt(time));

            //获取上个值的关盘价格
            if(i > 0){
                String oldTremd = Convert.toStr(trends.get(i - 1));
                String[] oldKinfo = Convert.toStr(oldTremd).split(",");
                BigDecimal oldlow = NumberUtil.round(oldKinfo[2], 2);
                stockInfo.add(oldlow);
            } else {
                stockInfo.add(low);
            }

            stockInfo.add(price);
            stockInfo.add(high);
            stockInfo.add(NumberUtil.round(kinfo[4], 2).doubleValue());
            stockInfo.add(low);
            stockInfo.add(vol);
            stockInfo.add(amount);
            dayKlineDatas.add(stockInfo);
        }

        if(symbol.contains("us")){
            symbol = symbol + "a";
        }

        return DayKlineRs.builder()
                .code(0)
                .data(dayKlineDatas)
                .name(name)
                .servertime(DateUtil.now())
                .symbol(symbol)
                .ticket(0)
                .version("2.0").build();
    }

    public static DayKlineRs getMinuteKlineData(String symbol){

        List<String> symbolInfo =
                Splitter.on(".").trimResults().splitToList(symbol);

        String secid = secidEnum.get(symbolInfo.get(1)) + symbolInfo.get(0);
        String uri = String.format(fzUrl,apiUrl,secid);
        String klineRs = doGet(uri);

        if(!klineRs.contains("klines")){
            //判断是否美股
            if("us".equals(symbolInfo.get(1))){
                secid = "106." + symbolInfo.get(0);
                uri = String.format(fzUrl,apiUrl,secid);
                klineRs = doGet(uri);
            } else {
                System.out.println("没有获取到K线数据");
                return null;
            }
        }

        JSONObject klineJson = JSON.parseObject(klineRs);

        JSONObject kdata = klineJson.getJSONObject("data");

        String preClose = kdata.getString("prePrice");

        String name = kdata.getString("name");

        JSONArray trends = kdata.getJSONArray("klines");

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        List<List<Object>> dayKlineDatas = Lists.newArrayList();

        for(int i=0;i<trends.size();i++){
            Object trand = trends.get(i);
            //分时K数据
            String[] kinfo = Convert.toStr(trand).split(",");

            Number number = NumberUtil.parseNumber(kinfo[6]);
            String amo = NumberUtil.toStr(number);
            BigInteger amount = new BigInteger(amo);

            BigDecimal avprice = NumberUtil.round(kinfo[7], 2);

            BigDecimal high = NumberUtil.round(kinfo[3], 2);

            BigDecimal low = NumberUtil.round(kinfo[2], 2);

            BigDecimal open = NumberUtil.round(kinfo[1], 2);

            BigDecimal price = open;

            //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
            BigDecimal chang_rate = null;
            if ((price).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(preClose).compareTo(new BigDecimal("0")) != 0) {
                chang_rate = (price).subtract(new BigDecimal(preClose));
                chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(preClose), 2, RoundingMode.HALF_UP);
            }

            BigDecimal risefall = price.subtract(new BigDecimal(preClose));

            BigDecimal increase = chang_rate;


            String time = DateUtil.format(DateUtil.parse(kinfo[0]), "yyyyMMdd");
            String klineTime = DateUtil.format(DateUtil.parse(kinfo[0]), "Hmm");
            if(symbol.contains("us")){
                //美股时间转早上显示
                DateTime dateTime = DateUtil.offsetHour(DateUtil.parse(kinfo[0]), 12);
                time = DateUtil.format(dateTime, "yyyyMMdd");

                DateTime dateTime1 = DateUtil.offsetHour(DateUtil.parse(kinfo[0]), 12);
                klineTime = DateUtil.format(dateTime1, "Hmm");
            }


            BigInteger vol = new BigInteger(kinfo[5]);

            List<Object> stockInfo = Lists.newLinkedList();
            stockInfo.add(Convert.toInt(time));

            //获取上个值的关盘价格
            if(i > 0){
                String oldTremd = Convert.toStr(trends.get(i - 1));
                String[] oldKinfo = Convert.toStr(oldTremd).split(",");
                BigDecimal oldlow = NumberUtil.round(oldKinfo[2], 2);
                stockInfo.add(oldlow);
            } else {
                stockInfo.add(low);
            }

            stockInfo.add(price);
            stockInfo.add(high);
            stockInfo.add(NumberUtil.round(kinfo[4], 2).doubleValue());
            stockInfo.add(low);
            stockInfo.add(vol);
            stockInfo.add(amount);
            stockInfo.add(Convert.toInt(klineTime));
            dayKlineDatas.add(stockInfo);
        }

        if(symbol.contains("us")){
            symbol = symbol + "a";
        }

        return DayKlineRs.builder()
                .code(0)
                .data(dayKlineDatas)
                .name(name)
                .servertime(DateUtil.now())
                .symbol(symbol)
                .ticket(0)
                .version("2.0").build();
    }

}
