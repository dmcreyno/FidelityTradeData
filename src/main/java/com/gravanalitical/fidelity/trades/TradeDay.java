/*
 * Copyright (c) 2019. Gravity Analytica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.gravanalitical.fidelity.trades;

import com.gravanalitical.fidelity.trades.format.TradeDayFormatFactory;
import com.gravanalitical.fidelity.trades.format.TradeDayPresentation;
import com.gravanalitical.locale.DisplayKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
    private static final Logger log = LogManager.getLogger("fidelity.trades.TradeDay");

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

    public String getDateStr() {
        return dateStr;
    }

    public ArrayList<TradeRecord> getTradeList() {
        return tradeList;
    }

    public List<TradePriceBucket> getTradePriceBuckets() {
        return tradePriceBuckets;
    }


    /**
     *
     * @return the average price for the day
     */
    public BigDecimal getAveragePrice() {
        return getDollarVolume().divide(getVolume(), GA_FidelityTradesConfig.getInstance().getMathScale(), RoundingMode.HALF_UP);
    }

    /**
     *
     * @return the volume for the day
     */
    public BigDecimal getVolume() {
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
    public BigDecimal getBuyVolume() {
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
    public BigDecimal getSellVolume() {
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
    public BigDecimal getUnknownVolume() {
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
    public BigDecimal getDollarVolume() {
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
    public BigDecimal getBuyDollarVolume() {
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
    public BigDecimal getSellDollarVolume() {
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
    public BigDecimal getUnknownDollarVolume() {
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


    @Override
    public String toString() {
        return TradeDayFormatFactory.getTabularFormatter().formatTradeDay(this);
    }

    public BigDecimal getPctBuyVol() {
        return getBuyVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }

    public BigDecimal getPctSellVol() {
        return getSellVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }

    public BigDecimal getPctUnknownVol() {
        return getUnknownVolume().divide(getVolume(),5,RoundingMode.HALF_UP);
    }

    public BigDecimal getPctBuyDolVol() {
        return getBuyDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }

    public BigDecimal getPctSellDolVol() {
        return getSellDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }

    public BigDecimal getPctUnknownDolVol() {
        return getUnknownDollarVolume().divide(getDollarVolume(),5,RoundingMode.HALF_UP);
    }

    public void writeSummary(PrintWriter psw, TradeDayPresentation formatter) {
        psw.println(formatter.formatTradeDay(this));
    }

    /**
     * Wrapper around a buffered reader. While there is not much value in wrapping that class
     * this class will skip the summary header info Fidelity puts in their exports.
     */
    public static class CSVInputReader {
        private static final Logger log = LogManager.getLogger("fidelity.trades");
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
                    log.info(DisplayKeys.get(DisplayKeys.PROCESSING_FILE_DATE), line);
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
                log.error(DisplayKeys.get(DisplayKeys.ERROR_FILE_CLOSE),file.getAbsolutePath(), e);
            }
        }
    }
}
