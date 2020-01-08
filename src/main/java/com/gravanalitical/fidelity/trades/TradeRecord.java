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
import org.apache.commons.text.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Data for a single trade.
 */
public class TradeRecord implements Comparable {
    private static final Logger log = LogManager.getLogger("fidelity.trades.TradeRecord");

    MathContext mathCtx = new MathContext(Integer.MAX_VALUE, RoundingMode.HALF_UP);

    public  enum BuySell {BUY, SELL, UNKOWN}

    private String timeStr;
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal size = BigDecimal.ZERO;
    private BigDecimal bid = BigDecimal.ZERO;
    private BigDecimal ask = BigDecimal.ZERO;

    public TradeRecord() {
    }

    /**
     * "Time","Last Price","Last Size","Bid Price","Ask Price",
     */
    public TradeRecord(String pData) {
        log.trace(DisplayKeys.get(DisplayKeys.LOG_PARSING), pData);
        StringTokenizer strtok = new StringTokenizer(pData,',');

        this.timeStr = strtok.next().replaceAll("\"","");
        this.price = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
        this.size = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
        try {
            this.bid = new BigDecimal(strtok.next().replaceAll("\"", ""), mathCtx);
        } catch(NumberFormatException nfex) {
            log.warn("Trade day has no bid info. Data: {}", pData);
        }
        try {
            this.ask = new BigDecimal(strtok.next().replaceAll("\"", ""), mathCtx);
        } catch(NumberFormatException nfex) {
            log.warn("Trade day has no ask info. Data: {}", pData);
        }
    }

    /**
     * Needed for unit tests.
     * @param pTimeString
     * @param pPrice
     * @param pSize
     * @param pBid
     * @param pAsk
     */
    public TradeRecord(String pTimeString, BigDecimal pPrice, BigDecimal pSize, BigDecimal pBid, BigDecimal pAsk) {
        this.timeStr = pTimeString;
        this.price = pPrice;
        this.size = pSize;
        this.bid = pBid;
        this.ask = pAsk;
    }

    /**
     * "Time","Last Price","Last Size","Bid Price","Ask Price",
     */
    static TradeRecord parse(String pData) {
        log.trace(DisplayKeys.get(DisplayKeys.LOG_PARSING), pData);
        return new TradeRecord(pData);
    }

    /**
     * Using the bid and the ask price this function calculates
     * a sentiment. If the trade is executed between the bid/ask then
     * the sentiment is unknown. However if the trade is executed at the
     * bid or at the ask then the trade is reported as a sell or a buy, respectively.
     * @return enum BuySell
     */
    public BuySell sentiment() {
        if(bid.equals(BigDecimal.ZERO) && ask.equals(BigDecimal.ZERO)) {
            return BuySell.UNKOWN;
        }

        if(bid.equals(ask)) {
            return BuySell.UNKOWN;
        }

        if(price.compareTo(bid) <= 0) {
            return BuySell.SELL;
        } else if(price.compareTo(ask) >= 0) {
            return BuySell.BUY;
        }
        return BuySell.UNKOWN;
    }

    BigDecimal getDollarVolume() {
        return this.price.multiply(this.size);
    }

    public BigDecimal getPrice() {
        return price;
    }

    BigDecimal getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeRecord that = (TradeRecord) o;
        return timeStr.equals(that.timeStr);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
     * for all {@code x} and {@code y}.  (This
     * implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
     * all {@code z}.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    public int compareTo(Object o) {
        TradeRecord other = (TradeRecord) o;
        if(other.timeStr == null || other.timeStr.trim().length() == 0) {
            throw new NullPointerException("argument cannot be null.");
        }
        return this.timeStr.compareTo(((TradeRecord) o).timeStr);
    }

    @Override
    public String toString() {
        return timeStr + "," +
                sentiment() + "," +
                price + "," +
                bid + "," +
                ask + "," +
                getDollarVolume();
    }
}
