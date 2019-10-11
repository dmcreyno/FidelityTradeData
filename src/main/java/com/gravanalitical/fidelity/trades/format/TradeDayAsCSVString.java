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

/**
 * Formats the trade day as a CSV string.
 */
public class TradeDayAsCSVString implements TradeDayPresentation {

    String delimiter = ",";

    public TradeDayAsCSVString() {

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
