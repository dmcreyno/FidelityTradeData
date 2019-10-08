package com.gravanalitical.fidelity.trades.format;

import com.gravanalitical.fidelity.trades.TradeDay;

/**
 * Formats the trade day as a CSV string.
 */
public class TradeDayAsCSVString implements TradeDayPresentation {

    String delimiter = ",";

    public TradeDayAsCSVString() {

    }
    public TradeDayAsCSVString(String pDelimiter) {
        delimiter = pDelimiter;
    }
    @Override
    public TradeDayPresentation setDelimiter(String pDelimiter) {
        delimiter = pDelimiter;
        return this;
    }
    /**
     * @param aTradeDay
     * @return
     */
    @Override
    public String formatTradeDay(TradeDay aTradeDay) {
            if(aTradeDay.getTradeList().isEmpty()) {
                return  aTradeDay.getDayOrdinal() + delimiter +
                        aTradeDay.getDateStr() + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0 + delimiter +
                        0;
            }

            StringBuilder recordString = new StringBuilder(aTradeDay.getDayOrdinal() + delimiter +
                    aTradeDay.getDateStr() + delimiter +
                    aTradeDay.getAveragePrice() + delimiter +
                    aTradeDay.getVolume() + delimiter +
                    aTradeDay.getBuyVolume() + delimiter +
                    aTradeDay.getSellVolume() + delimiter +
                    aTradeDay.getUnknownVolume() + delimiter +
                    aTradeDay.getDollarVolume() + delimiter +
                    aTradeDay.getBuyDollarVolume() + delimiter +
                    aTradeDay.getSellDollarVolume() + delimiter +
                    aTradeDay.getUnknownDollarVolume() + delimiter +
                    aTradeDay.getPctBuyVol() + delimiter +
                    aTradeDay.getPctSellVol() + delimiter +
                    aTradeDay.getPctUnknownVol() + delimiter +
                    aTradeDay.getPctBuyDolVol() + delimiter +
                    aTradeDay.getPctSellDolVol() + delimiter +
                    aTradeDay.getPctUnknownDolVol());

        aTradeDay.getTradePriceBuckets().forEach(aTradePriceBucket -> recordString.append(",").append(aTradePriceBucket.getPriceDollarVol()));


            return recordString.toString();
        }
}
