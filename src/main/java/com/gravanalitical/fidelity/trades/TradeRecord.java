package com.gravanalitical.fidelity.trades;

import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;


public class TradeRecord implements Comparable {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.TradeRecord");
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
        this.price = new BigDecimal(strtok.next().replaceAll("\"",""));
        this.size = new BigDecimal(strtok.next().replaceAll("\"",""));
        this.bid = new BigDecimal(strtok.next().replaceAll("\"",""));
        this.ask = new BigDecimal(strtok.next().replaceAll("\"",""));
    }

    /**
     * "Time","Last Price","Last Size","Bid Price","Ask Price",
     */
    static TradeRecord parse(String pData) {
        log.trace("parsing data: {}", pData);
        return new TradeRecord(pData);
    }

    BuySell sentiment() {
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

    private BigDecimal getPrice() {
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
