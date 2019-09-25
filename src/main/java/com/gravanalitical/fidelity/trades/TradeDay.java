/*******************************************************************************
 * Copyright (c) 2019.  Gravity Analytica
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.gravanalitical.fidelity.trades;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
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
     * The day-of-trading, 1, 2, 3, 4, 5, 6, etc of the collection of daily trade data input files.
     */
    private int dayOrdinal;

    /**
     * Date string. Currently provided as third line of csv file header.
     */

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
            dateStr = csvInputReader.getDate();
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
            log.debug("Asking trade bucket, {}, to accept trade price, {}.", aTradePriceBucket.getName(),tradeRecord.getPrice());
            if(aTradePriceBucket.acceptsTrade(tradeRecord)) {
                log.debug("Trade price {} was accepted by bucket, {}.",tradeRecord.getPrice(), aTradePriceBucket.getName());
            }
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

    public boolean isEmpty() {
        return this.tradeList.isEmpty();
    }

    public int getDayOrdinal() {
        return dayOrdinal;
    }

    public void setDayOrdinal(int pDayOrdinal) {
        dayOrdinal = pDayOrdinal;
    }

    public String toCSVString() {
        if(this.tradeList.isEmpty()) {
            return  dayOrdinal + "," +
                    dateStr + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0 + "," +
                    0;
        }

        StringBuilder recordString = new StringBuilder(dayOrdinal + "," +
                dateStr + "," +
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

    @Override
    public String toString() {
        if(this.tradeList.isEmpty()) {
            return  "No trades recorded.";
        }

        String rVal =
                "Summary for " + dateStr + "\n" +
                "Volumne      : " + getVolume() + "\n" +
                "Avg Price    : " + getAveragePrice() + "\n" +
                "Dollar-Volume: " + getDollarVolume() + "\n" +
                "Buy DV       : " + getBuyDollarVolume() + "\n" +
                "Sell DV      : " + getSellDollarVolume() + "\n" +
                "Unknown DV   : " + getUnknownDollarVolume() + "\n" +
                "Buy DV %     : " + getPctBuyDolVol().multiply(new BigDecimal("100"), new MathContext(3, RoundingMode.HALF_UP)) + "\n" +
                "Sell DV  %   : " + getPctSellDolVol().multiply(new BigDecimal("100"), new MathContext(3, RoundingMode.HALF_UP)) + "\n" +
                "Unknown DV % : " + getPctUnknownDolVol().multiply(new BigDecimal("100"), new MathContext(3, RoundingMode.HALF_UP)) ;


        return rVal;
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
        psw.println(this.toCSVString());
    }

    /**
     * Wrapper around a buffered reader. While there is not much value in wrapping that class
     * this class will skip the summary header info Fidelity puts in their exports.
     */
    public static class CSVInputReader {
        private static final Logger log = LoggerFactory.getLogger("fidelity.trades.CSVInputReader");
        private static final int LINE_NO_DATE = 2;
        private BufferedReader reader;
        private String dateStr;
        private File file;

        /**
         * CTOR accepting an instance of a File .
         * @param pFile the file to read from.
         */
        public CSVInputReader(File pFile) {
            file=pFile;
        }

        void initFile() throws IOException {
            reader = new BufferedReader(new FileReader(file));
            // throw away the first few lines (as set by )
            for (int i = 0; i < GA_FidelityTradesConfig.getInstance().getHeaderSkipLineCount(); i++) {
                String line = reader.readLine();
                if(i == LINE_NO_DATE) {
                    log.info("Processing file for date, {}.", line);
                    dateStr = line;
                }
            }
        }

        String getDate() {
            return this.dateStr;
        }

        String readLine() throws IOException {
            return reader.readLine();
        }

        void close() {
            try {
                reader.close();
            } catch (Exception e) {
                log.error("problem closing reader for {}",file.getAbsolutePath(), e);
            }
        }
    }
}
