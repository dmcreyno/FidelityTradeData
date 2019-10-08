package com.gravanalitical.fidelity.trades.format;

public class TradeDayFormatFactory {

    public enum FORMATTER {TABULAR, CSV}

    public static TradeDayPresentation getFormatter(FORMATTER requestedFormatter, String pDelimiter) {
        TradeDayPresentation rVal = TradeDayFormatFactory.getFormatter(requestedFormatter);
        rVal.setDelimiter(pDelimiter);
        return rVal;
    }

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
                throw new IllegalArgumentException("Unrecognized formatter specified, " + requestedFormatter);
        }

        return rVal;
    }

    public static TradeDayPresentation getCsvFormatter() {
        return getFormatter(FORMATTER.CSV);
    }


    public static TradeDayPresentation getTabularFormatter() {
        return getFormatter(FORMATTER.TABULAR);
    }
}
