package com.lr.stock.manager;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lr.stock.entity.*;
import com.lr.stock.utils.EastMoneyApi;
import com.lr.stock.utils.SignApi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

/**
 * 处理K线接口
 */
public class KlineManager {


    /**
     * 分时行情，17号接口处理
     * @return
     */
    public static Krs stockMarket17(String symbol){
        String stockInfo = SignApi.findSymbol(symbol);
        if(symbol.contains("hk")){
            return initStockDataHk17(stockInfo,symbol);
        } else if(symbol.contains("us")){
            return initStockDataUs17(stockInfo,symbol);
        } else {
            return initStockData17(stockInfo,symbol);
        }
    }

    /**
     * 分时行情16 接口
     * @param symbol
     * @return
     */
    public static Krs stockMarket16(String symbol){
        String stockInfo = SignApi.findSymbol(symbol);
        if(symbol.contains("hk")){
            return initStockDataHk16(stockInfo,symbol);
        } else if(symbol.contains("us")){
            return initStockDataUs16(stockInfo,symbol);
        } else {
            return initStockData16(stockInfo,symbol);
        }
    }

    /**
     * 分时行情11 接口
     * @param symbol
     * @return
     */
    public static Krs stockMarket11(String symbol){
        String stockInfo = SignApi.findSymbol(symbol);
        if(symbol.contains("hk")){
            return initStockDataHk11(stockInfo,symbol);
        } else if(symbol.contains("us")){
            return initStockDataUs11(stockInfo,symbol);
        } else {
            return initStockData11(stockInfo,symbol);
        }
    }

    /**
     * 分时行情13 接口
     * @param symbol
     * @return
     */
    public static Krs stockMarket13(String symbol){
        String stockInfo = SignApi.findSymbol(symbol);
        if(symbol.contains("hk")){
            return initStockDataHk13(stockInfo,symbol);
        } else if(symbol.contains("us")){
            return initStockDataUs13(stockInfo,symbol);
        } else {
            return initStockData13(stockInfo,symbol);
        }
    }

    /**
     * 封装 深圳 上海股票数据 [关联API ： 行情17]
     * @return
     */
    public static Krs initStockData17(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(9));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        List<Buy> buys = Lists.newArrayList();
        buys.add(Buy.builder()
                .price(Convert.toDouble(info.get(11)))
                .vol(Convert.toInt(info.get(10)))
                .build());
        buys.add(Buy.builder()
                .price(Convert.toDouble(info.get(13)))
                .vol(Convert.toInt(info.get(12)))
                .build());
        buys.add(Buy.builder()
                .price(Convert.toDouble(info.get(15)))
                .vol(Convert.toInt(info.get(14)))
                .build());
        buys.add(Buy.builder()
                .price(Convert.toDouble(info.get(17)))
                .vol(Convert.toInt(info.get(16)))
                .build());
        buys.add(Buy.builder()
                .price(Convert.toDouble(info.get(19)))
                .vol(Convert.toInt(info.get(18)))
                .build());
        List<Sell> sells = Lists.newArrayList();

        sells.add(Sell.builder()
                .price(Convert.toDouble(info.get(21)))
                .vol(Convert.toInt(info.get(20)))
                .build());
        sells.add(Sell.builder()
                .price(Convert.toDouble(info.get(23)))
                .vol(Convert.toInt(info.get(22)))
                .build());
        sells.add(Sell.builder()
                .price(Convert.toDouble(info.get(25)))
                .vol(Convert.toInt(info.get(24)))
                .build());
        sells.add(Sell.builder()
                .price(Convert.toDouble(info.get(27)))
                .vol(Convert.toInt(info.get(26)))
                .build());
        sells.add(Sell.builder()
                .price(Convert.toDouble(info.get(29)))
                .vol(Convert.toInt(info.get(28)))
                .build());

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(1));
        //当前价格
        Double price = Convert.toDouble(info.get(3));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(2));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(8));

        Stock stock = Stock.builder()
                .amount(amount)
                .buy(buys)
                .sell(sells)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.行情数据17")
                .build();

    }

    /**
     * 封装17接口 港股参数
     * @param stockInfo
     * @param symbol
     * @return
     */
    public static Krs initStockDataHk17(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(11));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(1);
        //开盘价格
        Double open = Convert.toDouble(info.get(2));
        //当前价格
        Double price = Convert.toDouble(info.get(6));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(3));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(12));

        List<Buy> buys = Lists.newArrayList();
        List<Sell> sells = Lists.newArrayList();
        //随机生成买入
        for(int i=0;i<5;i++){

            BigDecimal buydec = RandomUtil.randomBigDecimal(new BigDecimal("0.15"));
            BigDecimal round = NumberUtil.round(buydec, 2);
            double priceBuy = price.doubleValue() - round.doubleValue();
            int vols = RandomUtil.randomInt(0,2000);

            buys.add(Buy.builder()
                    .price(priceBuy)
                    .vol(vols)
                    .build());

            BigDecimal selldec = RandomUtil.randomBigDecimal(new BigDecimal("0.15"));
            BigDecimal sellround = NumberUtil.round(selldec, 2);
            double pricesell = price.doubleValue() - sellround.doubleValue();
            int volsell = RandomUtil.randomInt(0,2000);
            sells.add(Sell.builder()
                    .price(pricesell)
                    .vol(volsell)
                    .build());
        }

        Stock stock = Stock.builder()
                .amount(amount)
                .buy(buys)
                .sell(sells)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.港股行情数据17")
                .build();

    }


    public static Krs initStockDataUs17(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        String amo = info.get(30);
        String amlv = amo.substring(0,amo.indexOf("."));
        BigInteger amount = new BigInteger(amlv);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(6));
        //最低
        Double low = Convert.toDouble(info.get(7));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(5));
        //当前价格
        Double price = Convert.toDouble(info.get(1));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(26));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(10));

        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();


        List<Buy> buys = Lists.newArrayList();
        List<Sell> sells = Lists.newArrayList();
        //随机生成买入
        for(int i=0;i<5;i++){

            BigDecimal buydec = RandomUtil.randomBigDecimal(new BigDecimal("0.15"));
            BigDecimal round = NumberUtil.round(buydec, 2);
            double priceBuy = price.doubleValue() - round.doubleValue();
            int vols = RandomUtil.randomInt(0,2000);

            buys.add(Buy.builder()
                    .price(priceBuy)
                    .vol(vols)
                    .build());

            BigDecimal selldec = RandomUtil.randomBigDecimal(new BigDecimal("0.15"));
            BigDecimal sellround = NumberUtil.round(selldec, 2);
            double pricesell = price.doubleValue() - sellround.doubleValue();
            int volsell = RandomUtil.randomInt(0,2000);
            sells.add(Sell.builder()
                    .price(pricesell)
                    .vol(volsell)
                    .build());
        }

        Stock stock = Stock.builder()
                .amount(amount)
                .buy(buys)
                .sell(sells)
                .date(dateInt)
                .symbol(symbol + "a")
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.美股行情数据17")
                .build();

    }


    /**
     * 行情16接口
     * @param stockInfo
     * @param symbol
     * @return
     */
    public static Krs initStockData16(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(9));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(1));
        //当前价格
        Double price = Convert.toDouble(info.get(3));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(2));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(8));

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.行情数据16")
                .build();
    }


    public static Krs initStockDataHk16(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(11));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(1);
        //开盘价格
        Double open = Convert.toDouble(info.get(2));
        //当前价格
        Double price = Convert.toDouble(info.get(6));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(3));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(12));

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.港股行情数据16")
                .build();
    }

    public static Krs initStockDataUs16(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        String amo = info.get(30);
        String amlv = amo.substring(0,amo.indexOf("."));
        BigInteger amount = new BigInteger(amlv);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(6));
        //最低
        Double low = Convert.toDouble(info.get(7));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(5));
        //当前价格
        Double price = Convert.toDouble(info.get(1));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(26));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(10));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol + "a")
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.美股行情数据16")
                .build();
    }


    /**
     * 11接口处理
     * @param stockInfo
     * @param symbol
     * @return
     */
    public static Krs initStockData11(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(9));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(1));
        //当前价格
        Double price = Convert.toDouble(info.get(3));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(2));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(8));

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.行情数据11")
                .build();
    }


    public static Krs initStockDataHk11(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(11));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(1);
        //开盘价格
        Double open = Convert.toDouble(info.get(2));
        //当前价格
        Double price = Convert.toDouble(info.get(6));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(3));
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(12));

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.港股行情数据11")
                .build();
    }

    public static Krs initStockDataUs11(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        String amo = info.get(30);
        String amlv = amo.substring(0,amo.indexOf("."));
        BigInteger amount = new BigInteger(amlv);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(6));
        //最低
        Double low = Convert.toDouble(info.get(7));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(5));
        //当前价格
        Double price = Convert.toDouble(info.get(1));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(26));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(10));
        
        //当天涨幅 (当前价格 - 昨日关盘价格) * 100 - 昨日关盘价格
        BigDecimal chang_rate = null;
        if ((new BigDecimal(price)).compareTo(new BigDecimal("0")) != 0 && new BigDecimal(yclose).compareTo(new BigDecimal("0")) != 0) {
            chang_rate = (new BigDecimal(price)).subtract(new BigDecimal(yclose));
            chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(new BigDecimal(yclose), 2, RoundingMode.HALF_UP);
        }
        Double increase = chang_rate.doubleValue();

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol + "a")
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).increase(increase).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.美股行情数据11")
                .build();
    }

    /**
     * 13接口处理
     * @param stockInfo
     * @param symbol
     * @return
     */
    public static Krs initStockData13(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(9));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(1));
        //当前价格
        Double price = Convert.toDouble(info.get(3));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(2));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(8));

        //分时数据
        List<Minute> minuteData = EastMoneyApi.getMinuteData(symbol);

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .minute(minuteData)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.分时行情13")
                .build();
    }

    public static Krs initStockDataHk13(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        Number number = NumberUtil.parseNumber(info.get(11));
        String amo = NumberUtil.toStr(number);
        BigInteger amount = new BigInteger(amo);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(4));
        //最低
        Double low = Convert.toDouble(info.get(5));
        //名称
        String name = info.get(1);
        //开盘价格
        Double open = Convert.toDouble(info.get(2));
        //当前价格
        Double price = Convert.toDouble(info.get(6));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(3));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(12));

        //分时数据
        List<Minute> minuteData = EastMoneyApi.getMinuteData(symbol);

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol)
                .minute(minuteData)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.港股分时行情13")
                .build();
    }


    public static Krs initStockDataUs13(String stockInfo,String symbol){

        List<String> info = Splitter.on(",")
                .trimResults()
                .splitToList(stockInfo);

        String amo = info.get(30);
        String amlv = amo.substring(0,amo.indexOf("."));
        BigInteger amount = new BigInteger(amlv);

        String date = DateUtil.format(DateTime.now(), "yyyyMMdd");
        Integer dateInt = Convert.toInt(date);

        //最高
        Double high = Convert.toDouble(info.get(6));
        //最低
        Double low = Convert.toDouble(info.get(7));
        //名称
        String name = info.get(0);
        //开盘价格
        Double open = Convert.toDouble(info.get(5));
        //当前价格
        Double price = Convert.toDouble(info.get(1));
        //关盘价格
        Double yclose = Convert.toDouble(info.get(26));

        BigInteger time = new BigInteger("150002");

        BigInteger vol = new BigInteger(info.get(10));

        //分时数据
        List<Minute> minuteData = EastMoneyApi.getMinuteData(symbol);

        Stock stock = Stock.builder()
                .amount(amount)
                .date(dateInt)
                .symbol(symbol + "a")
                .minute(minuteData)
                .high(high).low(low).name(name).open(open).price(price).yclose(yclose).vol(vol).time(time)
                .build();

        return Krs.builder()
                .code(0)
                .count(1)
                .servertime(DateUtil.now())
                .start(0)
                .end(100)
                .stock(Lists.newArrayList(stock))
                .ticket(16)
                .version("V.美股分时行情13")
                .build();
    }

    /**
     * 5日分时
     * @param symbol
     * @return
     */
    public static FiveKlineRs fiveKlineData(String symbol){
        return EastMoneyApi.getFiveData(symbol);
    }

    public static DayKlineRs dayKlineData(String symbol){
        return EastMoneyApi.getDayKlineData(symbol);
    }

    public static DayKlineRs minuteKlineData(String symbol){
        return EastMoneyApi.getMinuteKlineData(symbol);
    }

}
