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
