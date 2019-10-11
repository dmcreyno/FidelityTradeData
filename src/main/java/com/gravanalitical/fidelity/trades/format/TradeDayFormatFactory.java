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

import com.gravanalitical.locale.DisplayKeys;

public class TradeDayFormatFactory {

    public enum FORMATTER {TABULAR, CSV}


    public static TradeDayPresentation getFormatter(FORMATTER requestedFormatter) {
        TradeDayPresentation rVal = null;
        switch (requestedFormatter) {
            case CSV:
                rVal = new TradeDayAsCSVString();
                break;
            case TABULAR:
                rVal = new TradeDayAsTabular();
                break;
            default:
                throw new IllegalArgumentException(DisplayKeys.get(DisplayKeys.ERROR_FORMATTER_UNKNOWN, requestedFormatter));
        }

        return rVal;
    }

    /**
     *
     * @return a formatter with the default delimiter, comma.
     */
    public static TradeDayPresentation getCsvFormatter() {
        return getFormatter(FORMATTER.CSV);
    }


    /**
     *
     * @return a formatter with the default delimiter, colon.
     */
    public static TradeDayPresentation getTabularFormatter() {
        return getFormatter(FORMATTER.TABULAR);
    }
}
