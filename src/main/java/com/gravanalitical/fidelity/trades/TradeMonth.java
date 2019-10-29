package com.gravanalitical.fidelity.trades;

import com.gravanalitical.fidelity.trades.config.GA_FidelityTradesConfig;
import com.gravanalitical.fidelity.trades.format.TradeMonthAsTabular;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Holds the days of trading. There really is no concept of a month in the application. It processes
 * all the CSV files found in the directory. It is assumed the user will be analyzing a month at a time.
 * However, there is no limit. The user may store more than one month of data in the directory and the
 * system will process it.
 */
public class TradeMonth {
    private BigDecimal totalVolume = BigDecimal.ZERO;
    private BigDecimal totalDollars = BigDecimal.ZERO;
    private BigDecimal totalBuyVolume = BigDecimal.ZERO;
    private BigDecimal totalSellVolume = BigDecimal.ZERO;
    private BigDecimal totalUnknownVolume = BigDecimal.ZERO;
    private BigDecimal totalBuyDollars = BigDecimal.ZERO;
    private BigDecimal totalSellDollars = BigDecimal.ZERO;
    private BigDecimal totalUnknownDollars = BigDecimal.ZERO;

    private GA_FidelityTradesConfig config;

    public TradeMonth(GA_FidelityTradesConfig pConfig) {
        config = pConfig;
    }


    public BigDecimal getVolume() {
        return totalVolume;
    }

    public void addTotalVolume(BigDecimal pArg) {
        totalVolume = totalVolume.add(pArg);
    }

    public BigDecimal getDollarVolume() {
        return totalDollars;
    }

    public void addTotalDollars(BigDecimal pArg) {
        totalDollars = totalDollars.add(pArg);
    }

    public BigDecimal getBuyVolume() {
        return totalBuyVolume;
    }

    public void addTotalBuyVolume(BigDecimal pArg) {
        totalBuyVolume = totalBuyVolume.add(pArg);
    }

    public BigDecimal getSellVolume() {
        return totalSellVolume;
    }

    public void addTotalSellVolume(BigDecimal pArg) {
        totalSellVolume = totalSellVolume.add(pArg);
    }

    public BigDecimal getUnknownVolume() {
        return totalUnknownVolume;
    }

    public void addTotalUnknownVolume(BigDecimal pArg) {
        totalUnknownVolume = totalUnknownVolume.add(pArg);
    }

    public BigDecimal getBuyDollarVolume() {
        return totalBuyDollars;
    }

    public void addTotalBuyDollars(BigDecimal pArg) {
        totalBuyDollars = totalBuyDollars.add(pArg);
    }

    public BigDecimal getSellDollarVolume() {
        return totalSellDollars;
    }

    public void addTotalSellDollars(BigDecimal pArg) {
        totalSellDollars = totalSellDollars.add(pArg);
    }

    public BigDecimal getUnknownDollarVolume() {
        return totalUnknownDollars;
    }

    public void addTotalUnknownDollars(BigDecimal pArg) {
        totalUnknownDollars = totalUnknownDollars.add(pArg);
    }

    /**
     * This is not going to be written to the CSV file, only the summary text. So, there is
     * no interface and no override.
     * @param psw
     * @param formatter
     */
    public void writeSummary(PrintWriter psw, TradeMonthAsTabular formatter) {
        psw.println(formatter.formatTradeMonth(this));
    }

    /**
     *
     * @return the average price for the month
     */
    public BigDecimal getAveragePrice() {
        return getDollarVolume().divide(getVolume(), config.getMathScale(), RoundingMode.HALF_UP);
    }
}
