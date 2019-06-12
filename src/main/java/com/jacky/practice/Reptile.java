package com.jacky.practice;

import com.jacky.common.util.HttpUtil;
import com.jacky.common.util.LogUtil;

import java.util.*;

import com.jacky.common.util.*;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.*;

/**
 * Description Here...
 *
 * @author Jacky Huang
 * @date 2018/3/14 14:40
 * @since jdk1.8
 */
public class Reptile {
    public static void main(String[] args) throws ScriptException {

        List<Stock> marketDataList = new StockMarketImpl().listMarketData(new String[]{
                "sz300748", "sz000795", "sh600366", "sz000060", "sh600704",
                "sz000959", "sh600111", "sh600549", "sh600259", "sh600058",
                "sh600259", "sz000758", "sh600111", "sz000969", "sz000970"});

        Map<String, List<Stock>> map = new HashMap<>();

        map.put("test1", marketDataList);
        map.put("test2", marketDataList);
        LogUtil.warn(JsonUtil.toJson(map));

//        Supplier<Stock> supplier=Stock::new;
//        supplier.get();

        marketDataList.forEach((stock) -> {
            String message = String.format(
                    "日期:%s,代码:%s,名称:%s,今开:%s,昨收:%s,最新:%s,最高:%s,最低:%s,总手:%s万手,金额:%s亿元,涨幅:%s%%",
                    stock.getTradingDay() + " " + stock.getUpdateTime(),
                    stock.getInstrumentID(),
                    stock.getInstrumentName(),
                    stock.getOpenPrice(),
                    stock.getClosePrice(),
                    stock.getLastPrice(),
                    stock.getHighestPrice(),
                    stock.getLowestPrice(),
                    stock.getVolume().divide(BigDecimal.valueOf(1000000), 2, BigDecimal.ROUND_HALF_UP),
                    stock.getTurnover().divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP),
                    stock.getClosePrice().compareTo(new BigDecimal(0)) > 0 ? stock.getLastPrice().subtract(stock.getClosePrice()).divide(stock.getClosePrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) : 0);
            if (stock.getLastPrice().compareTo(stock.getClosePrice()) > 0) {
                LogUtil.error(message);
            } else if (stock.getLastPrice().compareTo(stock.getClosePrice()) < 0) {
                LogUtil.warn(message);
            } else {
                LogUtil.info(message);
            }
        });

        if (marketDataList.size() > 0) {

            Comparator<Stock> comparator = (s1, s2) -> {

                if (s1.getClosePrice().compareTo(new BigDecimal(0)) > 0 && s2.getClosePrice().compareTo(new BigDecimal(0)) > 0) {
                    return s1.getLastPrice().subtract(s1.getClosePrice()).divide(s1.getClosePrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(s2.getLastPrice().subtract(s2.getClosePrice()).divide(s2.getClosePrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else if (s1.getClosePrice().compareTo(new BigDecimal(0)) > 0) {
                    return 1;
                } else if (s2.getClosePrice().compareTo(new BigDecimal(0)) > 0) {
                    return -1;
                }

                return 0;
            };

            Optional<Stock> stockOptional1 = marketDataList.stream().max(comparator);

            Optional<Stock> stockOptional2 = marketDataList.stream().min(comparator);

            // 数据分析
            Stock stock1 = stockOptional1.orElse(new Stock());
            LogUtil.info(String.format("涨幅最大的是：%s，%s%%", stock1.getInstrumentName(), stock1.getClosePrice().compareTo(new BigDecimal(0)) > 0 ? stock1.getLastPrice().subtract(stock1.getClosePrice()).divide(stock1.getClosePrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) : 0));

            Stock stock2 = stockOptional2.orElse(new Stock());
            LogUtil.info(String.format("跌幅最大的是：%s，%s%%", stock2.getInstrumentName(), stock2.getClosePrice().compareTo(new BigDecimal(0)) > 0 ? stock2.getLastPrice().subtract(stock2.getClosePrice()).divide(stock2.getClosePrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) : 0));

            // 成交量最大

            // 价格最高

            // 开盘和收盘差异最大
        }

        StockMarketImpl sm = new StockMarketImpl();
        if (IMarketService.class.isAssignableFrom(sm.getClass())) {
            LogUtil.info("OK");
        }
    }

    public static void scriptEngine() throws ScriptException {

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Compilable compilable = (Compilable) engine;
        //Local级别的Binding
        Bindings bindings = engine.createBindings();
        //定义函数并调用
        String script = "(1+0.1 * (F/100) * T)*P0";
        //解析编译脚本函数
        CompiledScript jsFunction = compilable.compile(script);

        //调用缓存着的脚本函数对象，Bindings作为参数容器传入
        bindings.put("F", 2.5);
        bindings.put("T", 30);
        bindings.put("A", 100);
        bindings.put("P0", 100);

        Object result = jsFunction.eval(bindings);
    }
}

interface IMarketService<T extends MarketData> {
    /**
     * getMarketData
     *
     * @param instrumentId
     * @return
     */
    T getMarketData(String instrumentId);

    /**
     * listMarketData
     *
     * @param arrInstrumentId
     * @return
     */
    default List<T> listMarketData(String[] arrInstrumentId) {
        return new ArrayList<>();
    }
}

class StockMarketImpl implements IMarketService<Stock> {
    private static final String MD_REQUEST_URL = "http://hq.sinajs.cn/list=%s";
    private static final int INT_31 = 31;

    @Override
    public Stock getMarketData(String instrumentId) {

        List<Stock> listStock = obtainMarketData(new String[]{instrumentId});

        return CollectionUtils.isEmpty(listStock) ? null : listStock.get(0);
    }

    @Override
    public List<Stock> listMarketData(String[] arrInstrumentId) {
        return obtainMarketData(arrInstrumentId);
    }

    private List<Stock> obtainMarketData(String[] arrInstrumentId) {
        String response = HttpUtil.get(String.format(MD_REQUEST_URL, StringUtils.join(arrInstrumentId, ",")), null);

        List<Stock> listStock = new ArrayList<>();
        if (!StringUtil.isEmtpy(response)) {
            String[] respList = response.split(";");
            Arrays.stream(respList).forEach((s) -> {

                Matcher idMatcher = compile("hq_str_(.*?)=").matcher(s);
                Matcher respMatcher = compile("(?<=\")(\\S+)(?=\")").matcher(s);
                if (idMatcher.find() && respMatcher.find()) {
                    String[] stockResp = respMatcher.group().split(",");
                    if (stockResp.length < INT_31) {
                        return;
                    }

                    Stock stock = new Stock();
                    stock.setInstrumentID(idMatcher.group(1));
                    stock.setInstrumentName(stockResp[0]);
                    stock.setTradingDay(stockResp[30]);
                    stock.setOpenPrice(new BigDecimal(stockResp[1]));
                    stock.setClosePrice(new BigDecimal(stockResp[2]));
                    stock.setHighestPrice(new BigDecimal(stockResp[4]));
                    stock.setLowestPrice(new BigDecimal(stockResp[5]));
                    stock.setLastPrice(new BigDecimal(stockResp[3]));
                    stock.setVolume(new BigDecimal(stockResp[8]));
                    stock.setTurnover(new BigDecimal(stockResp[9]));
                    stock.setUpdateTime(stockResp[31]);
                    stock.setUpdateMillisec("");

                    listStock.add(stock);
                }
            });
        }

        return listStock;
    }
}

@Data
class MarketData {

    private String tradingDay;
    private String instrumentID;
    private String instrumentName;
    private String exchangeID;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal lastPrice;
    private BigDecimal volume;
    private BigDecimal turnover;
    private BigDecimal settlementPrice;
    /**
     * 合约乘数
     */
    private BigDecimal volumeMultiple;
    /**
     * 最小变动
     */
    private BigDecimal priceTick;
    /**
     * 上市日期
     */
    private String openDate;
    /**
     * 到期日
     */
    private String expireDate;
    private String updateTime;
    private String updateMillisec;
}

@Data
class Futrues extends Stock {
}

@Data
class Stock extends MarketData {

    /**
     *
     * http://hq.sinajs.cn/list=sh601006
     *
     //var hq_str_sz300748="金力永磁,31.420,28.560,30.630,31.420,30.310,30.620,30.630,27873606,870833991.640,700,30.620,4900,30.610,26800,30.600,500,30.590,1500,30.580,407923,30.630,1600,30.640,23300,30.650,1600,30.660,4000,30.670,2019-05-22,15:00:03,00";
     //var hq_str_sh601006="大秦铁路,8.210,8.190,8.260,8.270,8.190,8.250,8.260,14275230,117472487.000,493243,8.250,100700,8.240,190200,8.230,148100,8.220,285600,8.210,5800,8.260,666170,8.270,751601,8.280,537700,8.290,569500,8.300,2019-05-22,11:30:00,00";
     * 0：”大秦铁路”，股票名字；
     * 1：”27.55″，今日开盘价；
     * 2：”27.25″，昨日收盘价；
     * 3：”26.91″，当前价格；
     * 4：”27.55″，今日最高价；
     * 5：”26.20″，今日最低价；
     * 6：”26.91″，竞买价，即“买一”报价；
     * 7：”26.92″，竞卖价，即“卖一”报价；
     * 8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
     * 9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
     * 10：”4695″，“买一”申请4695股，即47手；
     * 11：”26.91″，“买一”报价；
     * 12：”57590″，“买二”
     * 13：”26.90″，“买二”
     * 14：”14700″，“买三”
     * 15：”26.89″，“买三”
     * 16：”14300″，“买四”
     * 17：”26.88″，“买四”
     * 18：”15100″，“买五”
     * 19：”26.87″，“买五”
     * 20：”3100″，“卖一”申报3100股，即31手；
     * 21：”26.92″，“卖一”报价
     * (22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况”
     * 30：”2008-01-11″，日期；
     * 31：”15:05:32″，时间；
     */
}