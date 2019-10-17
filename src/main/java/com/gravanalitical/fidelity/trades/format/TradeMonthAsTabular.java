package com.gravanalitical.fidelity.trades.format;

import com.gravanalitical.fidelity.trades.TradeMonth;
import com.gravanalitical.locale.DisplayKeys;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TradeMonthAsTabular {
    NumberFormat percentageFormatter = new DecimalFormat("#.0%");
    NumberFormat shareVolumeFormatter = new DecimalFormat("#,###");
    NumberFormat usdFormatter = new DecimalFormat("$#,##0.00");
    NumberFormat usdTripsFormatter =    new DecimalFormat("$#,##0.000###");
    public String formatTradeMonth(TradeMonth pMonth) {
        String rVal =
                DisplayKeys.get(DisplayKeys.SUMMARY_REC_SEPARATOR) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_REC_SEPARATOR) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_OVERALL_HEADER) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_MONTH_VWAP, usdTripsFormatter.format(pMonth.getAveragePrice())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_VOLUME,shareVolumeFormatter.format(pMonth.getVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_BUY_VOL, shareVolumeFormatter.format(pMonth.getBuyVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_SELL_VOL, shareVolumeFormatter.format(pMonth.getSellVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_OTHER_VOL, shareVolumeFormatter.format(pMonth.getUnknownVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_DOLLAR_VOL, usdFormatter.format(pMonth.getDollarVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_BUY_DOLLAR_VOL, usdFormatter.format(pMonth.getBuyDollarVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_SELL_DOLLAR_VOL, usdFormatter.format(pMonth.getSellDollarVolume())) + "\n" +
                DisplayKeys.get(DisplayKeys.SUMMARY_OTHER_DOLLAR_VOL, usdFormatter.format(pMonth.getUnknownDollarVolume())) + "\n";

        return rVal;
    }
}
