package com.gravanalitical.fidelity.trades.format;

import com.gravanalitical.fidelity.trades.TradeDay;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TradeDayAsTabular implements TradeDayPresentation {
    String delimiter = ":";

    public TradeDayAsTabular() {}

    public TradeDayAsTabular(String pDelimiter) {
        delimiter = pDelimiter;
    }

    @Override
    public String formatTradeDay(TradeDay aTradeDay) {
        if(aTradeDay.getTradeList().isEmpty()) {
            return  "No trades recorded.";
        }

        // Create some formatters for the money and percentages and share volume
        NumberFormat percentageFormatter = new DecimalFormat("#.0%");
        NumberFormat shareVolumeFormatter = new DecimalFormat("#,###");
        NumberFormat usdFormatter = new DecimalFormat("$#,##0.00");
        NumberFormat usdTripsFormatter =    new DecimalFormat("$#,##0.000###");



        String rVal =
                "**** Summary for " + aTradeDay.getDateStr() + "****\n" +
                        "Avg Price\t\t"   + delimiter + " " + usdTripsFormatter.format(aTradeDay.getAveragePrice()) + "\n" +
                        "Volume\t\t\t"    + delimiter + " " + shareVolumeFormatter.format(aTradeDay.getVolume()) + "\n" +
                        "\tBuy Vol\t\t"   + delimiter + " " + shareVolumeFormatter.format(aTradeDay.getBuyVolume()) + "\n" +
                        "\tSell Vol\t"    + delimiter + " " + shareVolumeFormatter.format(aTradeDay.getSellVolume()) + "\n" +
                        "\tUnknown Vol\t" + delimiter + " " + shareVolumeFormatter.format(aTradeDay.getUnknownVolume()) + "\n" +
                        "Dollar-Volume\t" + delimiter + " " + usdFormatter.format(aTradeDay.getDollarVolume()) + "\n" +
                        "\tBuy DV\t\t"    + delimiter + " " + usdFormatter.format(aTradeDay.getBuyDollarVolume()) + "\n" +
                        "\tSell DV\t\t"   + delimiter + " " + usdFormatter.format(aTradeDay.getSellDollarVolume()) + "\n" +
                        "\tUnknown DV\t"  + delimiter + " " + usdFormatter.format(aTradeDay.getUnknownDollarVolume()) + "\n" +
                        "Buy DV %\t\t"    + delimiter + " " + percentageFormatter.format(aTradeDay.getPctBuyDolVol()) + "\n" +
                        "Sell DV  %\t\t"  + delimiter + " " + percentageFormatter.format(aTradeDay.getPctSellDolVol()) + "\n" +
                        "Unknown DV %\t"  + delimiter + " " + percentageFormatter.format(aTradeDay.getPctUnknownDolVol()) ;


        return rVal;
    }

    @Override
    public TradeDayPresentation setDelimiter(String pDelimiter) {
        delimiter = pDelimiter;
        return this;
    }
}
