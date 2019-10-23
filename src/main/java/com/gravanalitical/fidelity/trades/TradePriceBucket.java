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

import com.gravanalitical.locale.DisplayKeys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Each TradeDay gets its own set of price buckets to show the distribution.
 * Three metrics are available.
 * <ul>
 *     <li>Trade Count: the number of trades for this bucket.</li>
 *     <li>Price Volume: the number of shares traded for this bucket.</li>
 *     <li>Price Dollar-Volume: The amount of money traded for this bucket. Demonstrates better than
 *     anything else, where the money is going.</li>
 * </ul>
 */
public class TradePriceBucket {
    private static final Logger log = LogManager.getLogger("fidelity.trades.TradePriceBucket");
    private String name;
    private BigDecimal min;
    private BigDecimal max;
    private BigInteger tradeCount = BigInteger.ZERO;
    private BigDecimal priceDollarVol = BigDecimal.ZERO;
    private BigDecimal priceVolume = BigDecimal.ZERO;

    public  enum COMPARISON_LOGIC {INCLUSIVE,EXCLUSIVE}

    public COMPARISON_LOGIC compLogic = COMPARISON_LOGIC.INCLUSIVE;

    public TradePriceBucket(String name, BigDecimal min, BigDecimal max, COMPARISON_LOGIC compLogic) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.compLogic = compLogic;
    }

    public BigDecimal getPriceDollarVol() {
        return priceDollarVol;
    }

    public BigDecimal getPriceVolume() {
        return priceVolume;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigInteger getTradeCount() {
        return tradeCount;
    }

    public COMPARISON_LOGIC getComparisonLogic() {
        return this.compLogic;
    }

    /**
     * Examines the trade to see if it belongs in this bucket.
     * @param tradeRecord
     * @return true if this bucket accepted the trade record.
     */
    public boolean acceptsTrade(TradeRecord tradeRecord) {
        boolean rVal = belongsInThisBucket(tradeRecord.getPrice());
        if(rVal) {
            this.priceDollarVol = this.priceDollarVol.add(tradeRecord.getPrice().multiply(tradeRecord.getSize()));
            this.priceVolume = this.priceVolume.add(tradeRecord.getSize());
        }
        return rVal;
    }

    private boolean belongsInThisBucket (BigDecimal pPrice) {
        boolean rVal = false;
        if(compLogic == COMPARISON_LOGIC.INCLUSIVE) {
            rVal = testInclusive(pPrice);
        } else if(compLogic == COMPARISON_LOGIC.EXCLUSIVE){
            rVal = testExclusive(pPrice);
        } else {
            // something has gone horribly wrong
            // maybe the compLogic var was not initialized???
            log.error(DisplayKeys.get(DisplayKeys.ERROR_COMP_LOGIC), compLogic);
            throw new IllegalArgumentException("unrecognized comparison logic value");
        }

        if(rVal) {
            tradeCount = tradeCount.add(BigInteger.ONE);
        }

        return rVal;
    }

    private boolean testInclusive(BigDecimal pPrice) {
        return pPrice.compareTo(this.min) >= 0
                && pPrice.compareTo(this.max) <= 0;
    }

    private boolean testExclusive(BigDecimal pPrice) {
        return pPrice.compareTo(this.min) > 0
                && pPrice.compareTo(this.max) < 0;
    }

    public String toString() {
        return this.name + ", " +
                this.min + ", " +
                this.max + ", " +
                this.compLogic;
    }
}
