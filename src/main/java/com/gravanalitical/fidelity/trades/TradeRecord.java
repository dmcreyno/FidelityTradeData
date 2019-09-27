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

import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Data for a single trade.
 */
public class TradeRecord implements Comparable {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.TradeRecord");

    MathContext mathCtx = new MathContext(Integer.MAX_VALUE, RoundingMode.HALF_UP);

    public  enum BuySell {BUY, SELL, UNKOWN}

    private String timeStr;
    private BigDecimal price;
    private BigDecimal size;
    private BigDecimal bid;
    private BigDecimal ask;

    public TradeRecord() {
    }

    /**
     * "Time","Last Price","Last Size","Bid Price","Ask Price",
     */
    public TradeRecord(String pData) {
        log.trace("parsing data: {}", pData);
        StringTokenizer strtok = new StringTokenizer(pData,',');

        this.timeStr = strtok.next().replaceAll("\"","");
        this.price = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
        this.size = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
        this.bid = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
        this.ask = new BigDecimal(strtok.next().replaceAll("\"",""),mathCtx);
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
        log.trace("parsing data: {}", pData);
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
                getPrice() + "," +
                getDollarVolume();
    }
}
