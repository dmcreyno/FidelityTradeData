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
