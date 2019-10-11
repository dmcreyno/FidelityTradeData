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
package com.gravanalitical.locale;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class to encapsulate internationalization features.
 */
public class DisplayKeys {
    private static final Logger log = LogManager.getLogger("fidelity.trades");

    public static final String STARTUP                      = "messageKey.startup";
    public static final String PROCESSING                   = "messageKey.processing";
    public static final String PROCESSING_FILE              = "messageKey.processing.file";
    public static final String PROCESSING_FILE_DATE         = "messageKey.processing.file.date";
    public static final String PROCESSING_OUTPUT_FILE       = "messageKey.processing.output.file";
    public static final String SUMMARY_REC_SEPARATOR        = "messageKey.record.separator";
    public static final String SUMMARY_HEADER               = "messageKey.summary.header";
    public static final String SUMMARY_AVG_PRICE            = "messageKey.summary.avgPrice";
    public static final String SUMMARY_VOLUME               = "messageKey.summary.vol";
    public static final String SUMMARY_BUY_VOL              = "messageKey.summary.buyVol";
    public static final String SUMMARY_SELL_VOL             = "messageKey.summary.sellVol";
    public static final String SUMMARY_OTHER_VOL            = "messageKey.summary.otherVol";
    public static final String SUMMARY_DOLLAR_VOL           = "messageKey.summary.dollarVol";
    public static final String SUMMARY_BUY_DOLLAR_VOL       = "messageKey.summary.buyDolVol";
    public static final String SUMMARY_SELL_DOLLAR_VOL      = "messageKey.summary.sellDolVol";
    public static final String SUMMARY_OTHER_DOLLAR_VOL     = "messageKey.summary.otherDolVol";
    public static final String SUMMARY_BUY_DOLLAR_VOL_PCT   = "messageKey.summary.buyDolVolPct";
    public static final String SUMMARY_SELL_DOLLAR_VOL_PCT  = "messageKey.summary.sellDolVolPct";
    public static final String SUMMARY_OTHER_DOLLAR_VAL_PCT = "messageKey.summary.otherDolVolPct";
    public static final String SKIPPING_HIDDEN_FILE         = "messageKey.skipping.hidden";
    public static final String ERROR                        = "messageKey.error";
    public static final String ERROR_PROC_FILE              = "messageKey.error.processing.file";
    public static final String ERROR_FILE_CLOSE             = "messageKey.error.file.close";
    public static final String ERROR_COMP_LOGIC             = "messageKey.error.unrecognized.logic";
    public static final String ERROR_FORMATTER_UNKNOWN      = "messageKey.error.formatter.unrecognized";
    public static final String LOG_PARSING                  = "messageKey.trace.parsing.data";

    private static DisplayKeys _instance = new DisplayKeys();
    private ResourceBundle displayKeys;

    private DisplayKeys() {
        Locale currentLocale = Locale.getDefault();
        log.info("Locale: {}", currentLocale);
        displayKeys = ResourceBundle.getBundle("DisplayKeys", currentLocale);
    }

    /**
     *
     * @param key
     * @param arguments
     * @return
     */
    public static String get(String key, Object ... arguments) {
        return DisplayKeys.getInstance().getMessageInternal(key,arguments);
    }

    private static DisplayKeys getInstance() {
        return _instance;
    }

    private String getMessageInternal(String key, Object ... arguments) {
        return MessageFormat.format(displayKeys.getString(key), arguments);
    }

    public static void main(String[] args) {
        Locale currentLocale = Locale.getDefault();
        System.out.println(currentLocale);
        System.out.println(currentLocale.getDisplayLanguage());
        System.out.println(currentLocale.getDisplayCountry());

        System.out.println(currentLocale.getLanguage());
        System.out.println(currentLocale.getCountry());

        System.out.println(System.getProperty("user.language"));
        System.out.println(System.getProperty("user.country"));
    }
}
