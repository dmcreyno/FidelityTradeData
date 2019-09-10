package com.gravanalitical.fidelity.trades;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class TradePriceBucketTest {

    @Test
    public void acceptsTradeExactly() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0004"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        TradePriceBucket aTradePriceBucket = new TradePriceBucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aTradePriceBucket.acceptsTrade(tr));
    }

    @Test
    public void rejectsTradeExactly() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0005"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        TradePriceBucket aTradePriceBucket = new TradePriceBucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aTradePriceBucket.acceptsTrade(tr));
    }

    @Test
    public void acceptsTradeInclusive() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0004"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        TradePriceBucket aTradePriceBucket = new TradePriceBucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0005"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aTradePriceBucket.acceptsTrade(tr));
        aTradePriceBucket = new TradePriceBucket("0003", new BigDecimal("0.0003"), new BigDecimal("0.0004"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aTradePriceBucket.acceptsTrade(tr));
    }

    @Test
    public void rejectsTradeInclusive() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0005"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        TradePriceBucket aTradePriceBucket = new TradePriceBucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aTradePriceBucket.acceptsTrade(tr));
        aTradePriceBucket = new TradePriceBucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), TradePriceBucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aTradePriceBucket.acceptsTrade(tr));
    }

}