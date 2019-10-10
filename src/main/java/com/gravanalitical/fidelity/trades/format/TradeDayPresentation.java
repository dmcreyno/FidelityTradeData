package com.gravanalitical.fidelity.trades.format;

import com.gravanalitical.fidelity.trades.TradeDay;

/**
 * Interface allowing the development of different
 * formatting strategies for a TradeDay object.
 *
 * Created to move the presentation logic out of the
 * TradeDay class.
 *
 * In your implementation, do not store state. The system is
 * going to assume the impls are thread safe. Without "state"
 * they are.
 *
 */
public interface TradeDayPresentation {
    /**
     *
     * @param aTradeDay
     * @return
     */
    String formatTradeDay(TradeDay aTradeDay);
}
