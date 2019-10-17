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

package com.gravanalitical.fidelity.trades.format;

import com.gravanalitical.fidelity.trades.TradeDay;
import com.gravanalitical.locale.DisplayKeys;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TradeDayAsTabular implements TradeDayPresentation {

    public TradeDayAsTabular() {}


    @Override
    public String formatTradeDay(TradeDay aTradeDay) {
        if(aTradeDay.getTradeList().isEmpty()) {
            return  "No trades recorded.";
        }

        // Create some formatters for the money and percentages and share volume
        NumberFormat percentageFormatter = new DecimalFormat("0.0##%");
        NumberFormat shareVolumeFormatter = new DecimalFormat("#,###");
        NumberFormat usdFormatter = new DecimalFormat("$#,##0.00");
        NumberFormat usdTripsFormatter =    new DecimalFormat("$#,##0.000###");


        String rVal =
                DisplayKeys.get(DisplayKeys.SUMMARY_REC_SEPARATOR) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_HEADER, aTradeDay.getDateStr()) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_AVG_PRICE,usdTripsFormatter.format(aTradeDay.getAveragePrice())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_VOLUME,shareVolumeFormatter.format(aTradeDay.getVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_BUY_VOL, shareVolumeFormatter.format(aTradeDay.getBuyVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_SELL_VOL, shareVolumeFormatter.format(aTradeDay.getSellVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_OTHER_VOL, shareVolumeFormatter.format(aTradeDay.getUnknownVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_DOLLAR_VOL, usdFormatter.format(aTradeDay.getDollarVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_BUY_DOLLAR_VOL, usdFormatter.format(aTradeDay.getBuyDollarVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_SELL_DOLLAR_VOL, usdFormatter.format(aTradeDay.getSellDollarVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_OTHER_DOLLAR_VOL, usdFormatter.format(aTradeDay.getUnknownDollarVolume())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_BUY_DOLLAR_VOL_PCT, percentageFormatter.format(aTradeDay.getPctBuyDolVol())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_SELL_DOLLAR_VOL_PCT, percentageFormatter.format(aTradeDay.getPctSellDolVol())) + "\n" +
                        DisplayKeys.get(DisplayKeys.SUMMARY_OTHER_DOLLAR_VAL_PCT, percentageFormatter.format(aTradeDay.getPctUnknownDolVol())) ;

        return rVal;
    }

}
