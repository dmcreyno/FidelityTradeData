package com.gravanalitical.fidelity.trades;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Bucket {
    private String name;
    private BigDecimal min = BigDecimal.ZERO;
    private BigDecimal max = BigDecimal.ZERO;
    private BigInteger tradeCount = BigInteger.ZERO;
    public  enum COMPARISON_LOGIC {INCLUSIVE,EXCLUSIVE}
    public COMPARISON_LOGIC compLogic = COMPARISON_LOGIC.INCLUSIVE;

    public Bucket(String name, BigDecimal min, BigDecimal max, COMPARISON_LOGIC compLogic) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.compLogic = compLogic;
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
        return belongsInThisBucket(tradeRecord.getPrice());
    }

    private boolean belongsInThisBucket (BigDecimal pPrice) {
        boolean rVal = false;
        if(this.compLogic == COMPARISON_LOGIC.INCLUSIVE) {
            rVal = testInclusive(pPrice);
        } else {
            rVal = testExclusive(pPrice);
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
