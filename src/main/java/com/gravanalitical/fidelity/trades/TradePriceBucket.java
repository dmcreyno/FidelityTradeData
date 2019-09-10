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
        if(this.compLogic == COMPARISON_LOGIC.INCLUSIVE) {
            rVal = testInclusive(pPrice);
        } else if(this.compLogic == COMPARISON_LOGIC.EXCLUSIVE){
            rVal = testExclusive(pPrice);
        } else {
            // something has gone horribly wrong
            // maybe the compLogic var was not initialized???
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
