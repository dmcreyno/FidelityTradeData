package com.gravanalitical.fidelity.trades;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BucketTest {

    @Test
    public void acceptsTradeExactly() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0004"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        Bucket aBucket = new Bucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aBucket.acceptsTrade(tr));
    }

    @Test
    public void rejectsTradeExactly() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0005"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        Bucket aBucket = new Bucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aBucket.acceptsTrade(tr));
    }

    @Test
    public void acceptsTradeInclusive() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0004"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        Bucket aBucket = new Bucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0005"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aBucket.acceptsTrade(tr));
        aBucket = new Bucket("0003", new BigDecimal("0.0003"), new BigDecimal("0.0004"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertTrue(aBucket.acceptsTrade(tr));
    }

    @Test
    public void rejectsTradeInclusive() {
        TradeRecord tr = new TradeRecord(
                "20190101",
                new BigDecimal("0.0005"),
                new BigDecimal("1000"),
                new BigDecimal("0.0004"),
                new BigDecimal("0.0005"));
        Bucket aBucket = new Bucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aBucket.acceptsTrade(tr));
        aBucket = new Bucket("0004", new BigDecimal("0.0004"), new BigDecimal("0.0004"), Bucket.COMPARISON_LOGIC.INCLUSIVE);
        Assert.assertFalse(aBucket.acceptsTrade(tr));
    }

}