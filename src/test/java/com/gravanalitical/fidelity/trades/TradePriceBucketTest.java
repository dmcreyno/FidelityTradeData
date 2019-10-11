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