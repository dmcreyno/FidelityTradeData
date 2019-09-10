package com.gravanalitical.fidelity.trades;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the data for one day of trading and stores the stats. Keeps the trades
 * in a list. Also, puts the dollar volume into <i>buckets</i> which are
 * defined in the configuration properties files.
 */
public class TradeDay {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.TradeDay");
    /**
     * The date for which the data has been stored. Format: yyyymmdd.
     */
    private String dateStr;

    /**
     *
     */
    private ArrayList<TradeRecord> tradeList = new ArrayList<>();

    /**
     *
     */
    private File aFile;

    /**
     * Collection of price range tradePriceBuckets to count trades
     * that execute between the min and max defined for
     * the bucket.
     */
    private List<TradePriceBucket> tradePriceBuckets;

    /**
     * The data comes as a CSV of trades for one day.
     */
    public TradeDay(File pFile) {
        aFile = pFile;
        dateStr = pFile.getName().substring(0,8);
        tradePriceBuckets = GA_FidelityTradesConfig.getInstance().getBuckets();
    }

    /**
     * Reads the File for the day. Puts the trade dollar-volume in the
     * appropriate bucket.
     */
    void process() {
        CSVInputReader csvInputReader = new CSVInputReader(aFile);
        try {
            csvInputReader.initFile();
        } catch (IOException e) {
            log.error("reader initiation failed.",e);
            System.exit(-1);
        }
        try {
            String currentLine = csvInputReader.readLine();
            log.trace("throwing away header [{}]",currentLine);
            long lineCounter = 1L;
            boolean done = false;
            while(!done) {
                if ((currentLine = csvInputReader.readLine()) == null
                || currentLine.startsWith("\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",")) {
                    done = true;
                } else {

                    if (log.isTraceEnabled()) log.trace("LINE: {}", currentLine);

                    try {
                        TradeRecord tr = TradeRecord.parse(currentLine);
                        if (log.isDebugEnabled()) log.debug("adding a trade . . . {}", tr);
                        this.tradeList.add(tr);
                        distributeToBucket(tr);
                    } catch (Exception e) {
                        log.error("error processing line {} in file {}", lineCounter,aFile.getName());
                        log.error("error processing data, \"{}\"", currentLine, e);
                    }
                } // end if check for end of file
                lineCounter++;
            } // end while not done
        } catch (IOException e) {
            log.error("reading file failed: {}", aFile.getAbsolutePath(), e);
        } finally {
            csvInputReader.close();
        }
    }

    /**
     * Put the trade in a bucket.
     */

    private void distributeToBucket(TradeRecord tradeRecord) {
        tradePriceBuckets.forEach(aTradePriceBucket -> {
            if(aTradePriceBucket.acceptsTrade(tradeRecord)) return;
        });
    }

    /**
     *
     * @return the average price for the day
     */
    private BigDecimal getAveragePrice() {
        return getDollarVolume().divide(getVolume(),5, RoundingMode.HALF_UP);
    }

    /**
     *
     * @return the volume for the day
     */
    private BigDecimal getVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord tradeRecord : tradeList) {
            rVal = rVal.add(tradeRecord.getSize());
        }
        return rVal;
    }

    /**
     *
     * @return the buy volume for the day
     */
    private BigDecimal getBuyVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.BUY == trade.sentiment()) {
                rVal = rVal.add(trade.getSize());
            }
        }
        return rVal;
    }


    /**
     *
     * @return the sell volume for the day
     */
    private BigDecimal getSellVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.SELL == trade.sentiment()) {
                rVal = rVal.add(trade.getSize());
            }
        }
        return rVal;
    }

    /**
     *
     * @return the unknown volume for the day
     */
    private BigDecimal getUnknownVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.UNKOWN == trade.sentiment()) {
                rVal = rVal.add(trade.getSize());
            }
        }
        return rVal;
    }

    /**
     *
     * @return the dollar volume for the day
     */
    private BigDecimal getDollarVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord tradeRecord : tradeList) {
            rVal = rVal.add(tradeRecord.getDollarVolume());
        }
        return rVal;
    }

    /**
     *
     * @return the dollar buy volume for the day
     */
    private BigDecimal getBuyDollarVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.BUY == trade.sentiment()) {
                rVal = rVal.add(trade.getDollarVolume());
            }
        }
        return rVal;
    }

    /**
     *
     * @return the dollar sell volume for the day
     */
    private BigDecimal getSellDollarVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.SELL == trade.sentiment()) {
                rVal = rVal.add(trade.getDollarVolume());
            }
        }
        return rVal;
    }

    /**
     *
     * @return the dollar unknown volume for the day
     */
    private BigDecimal getUnknownDollarVolume() {
        BigDecimal rVal = BigDecimal.ZERO;

        for (TradeRecord trade : tradeList) {
            if (TradeRecord.BuySell.UNKOWN == trade.sentiment()) {
                rVal = rVal.add(trade.getDollarVolume());
            }
        }
        return rVal;
    }

    @Override
    public String toString() {
        StringBuilder recordString = new StringBuilder(dateStr + "," +
                getAveragePrice() + "," +
                getVolume() + "," +
                getBuyVolume() + "," +
                getSellVolume() + "," +
                getUnknownVolume() + "," +
                getDollarVolume() + "," +
                getBuyDollarVolume() + "," +
                getSellDollarVolume() + "," +
                getUnknownDollarVolume() + "," +
                getPctBuyVol() + "," +
                getPctSellVol() + "," +
                getPctUnknownVol() + "," +
                getPctBuyDolVol() + "," +
                getPctSellDolVol() + "," +
                getPctUnknownDolVol());

        tradePriceBuckets.forEach(aTradePriceBucket -> recordString.append(",").append(aTradePriceBucket.getPriceDollarVol()));


        return recordString.toString();
    }

    private BigDecimal getPctBuyVol() {
        return getBuyVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }
    private BigDecimal getPctSellVol() {
        return getSellVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }
    private BigDecimal getPctUnknownVol() {
        return getUnknownVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }
    private BigDecimal getPctBuyDolVol() {
        return getBuyDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }
    private BigDecimal getPctSellDolVol() {
        return getSellDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }
    private BigDecimal getPctUnknownDolVol() {
        return getUnknownDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }

    void writeSummary(PrintWriter psw) {
        psw.println(this.toString());
    }
}
