/*******************************************************************************
 * Copyright (c) 2019.  Gravity Analytica
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.gravanalitical.fidelity.trades;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Reads the "fidelity.properties file referenced on the command line
 * using the -D option where the base directory is defined.
 * -Dcom.ga.fidelity.trades.home=/users/mary/trade_data/MSFT
 */
class GA_FidelityTradesConfig {
    private static final Logger log = LogManager.getLogger("fidelity.trades");

    private Locale locale = new Locale("en", "US");
    private ResourceBundle displayKeys = ResourceBundle.getBundle("DisplayKeys", locale);

    static class PropertyConstants {
        private static final String HOME_KEY                      = "com.ga.fidelity.trades.home";
        private static final String OUTPUT_HEADER_LINE_01         = "com.ga.fidelity.trades.output.header1";
        private static final String HEADER_SKIP_LINE_COUNT        = "com.ga.fidelity.trades.skip.header";
        private static final String TICKER                        = "com.ga.fidelity.trades.ticker";
        private static final String BUCKET_NAMES                  = "com.ga.fidelity.trades.bucket.names"; //=0001,0002
        private static final String BUCKET_MINS                   = "com.ga.fidelity.trades.bucket.mins";  //=0.00001,0.00019
        private static final String BUCKET_MAXS                   = "com.ga.fidelity.trades.bucket.maxs";  //=0.00020,0.000299
        private static final String BUCKET_LOGIC                  = "com.ga.fidelity.trades.bucket.logx";  //=INCLUSIVE,INCLUSIVE
    }

    /**
     * Marker used to help filter logging statements for appenders
     */
    public static final Marker DEV_MARKER = MarkerManager.getMarker("DEV");
    /**
     * Marker used to help filter logging statements for appenders
     */
    public static final Marker PRINT_MARKER = MarkerManager.getMarker("PRINT");

    /**
     * The file extension for a CSV file (csv)
     */
    public static final String CSV_FILE_EXTENSION = "csv";

    /**
     * The extensions of the files to process from the input dir.
     */
    public static final String[] FILE_EXT_FOR_PROCESSING = {CSV_FILE_EXTENSION};

    private static final GA_FidelityTradesConfig _instance = new GA_FidelityTradesConfig();

    private static String baseDir;
    private static String fileSeparator;
    private static Configuration config;

    private GA_FidelityTradesConfig() {
        baseDir = System.getProperty(PropertyConstants.HOME_KEY);
        fileSeparator = System.getProperty("file.separator");
        log.info("BASE_DIR: " + baseDir);
        if(null == baseDir) {
            throw new Error("based directory is null. Did you set the command line property, \"-D" + PropertyConstants.HOME_KEY + "?\"");
        }
        if(log.isDebugEnabled()) {
            log.debug("FILE SEP: " + fileSeparator);
        }

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class);

        builder.configure(params.properties().setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                .setFileName(baseDir+fileSeparator+"fidelity.properties"));

        try {
            config = builder.getConfiguration();
        } catch(ConfigurationException cex) {
            log.error("configuration failed.",cex);
            System.exit(-1);
        } finally {

        }
    }

    static GA_FidelityTradesConfig getInstance() {
        return _instance;
    }

    String getHomeDir() {
        return baseDir;
    }

    int getHeaderSkipLineCount() {
        return config.getInt(PropertyConstants.HEADER_SKIP_LINE_COUNT);
    }

    String getTicker() {
        return config.getString(PropertyConstants.TICKER);
    }

    String getInputDir() {
        return baseDir+fileSeparator+"input";
    }

    public String getOutputHeader() {
        // There are two rows to the header with different numbers of columns.
        List columnNames = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        int listSize;
        columnNames = config.getList(PropertyConstants.OUTPUT_HEADER_LINE_01);
        listSize = columnNames.size();
        for(int i = 0; i < listSize; i++) {
            buffer.append(columnNames.get(i));
            if(i != listSize-1) {
                buffer.append(",");
            }
        }
        // append the bucket list
        buffer.append(getBucketListHeader());
        return  buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(GA_FidelityTradesConfig.getInstance().getOutputHeader());
    }

    public List<TradePriceBucket> getBuckets() {
        List<TradePriceBucket> tradePriceBucketList = new ArrayList<>();
        List bucketNames = config.getList(PropertyConstants.BUCKET_NAMES);
        List bucketMaxs = config.getList(PropertyConstants.BUCKET_MAXS);
        List bucketMins = config.getList(PropertyConstants.BUCKET_MINS);
        List bucketLogxes = config.getList(PropertyConstants.BUCKET_LOGIC);
        int bucketListSize = bucketNames.size();
        for(int i = 0; i < bucketListSize; i++) {
            // TradePriceBucket(String name, BigDecimal min, BigDecimal max, COMPARISON_LOGIC compLogic)
            String bucketName = (String) bucketNames.get(i);
            BigDecimal bucketMin = new BigDecimal((String)bucketMins.get(i));
            BigDecimal bucketMax = new BigDecimal((String)bucketMaxs.get(i));
            TradePriceBucket.COMPARISON_LOGIC bucketLogx = TradePriceBucket.COMPARISON_LOGIC.valueOf((String)bucketLogxes.get(i));
            TradePriceBucket aTradePriceBucket = new TradePriceBucket(bucketName,bucketMin,bucketMax,bucketLogx);
            log.debug("TradePriceBucket: {}", aTradePriceBucket);
            tradePriceBucketList.add(aTradePriceBucket);
        }

        return tradePriceBucketList;
    }

    private String getBucketListHeader() {
        StringBuilder buffer = new StringBuilder();
        GA_FidelityTradesConfig.getInstance().getBuckets().forEach(aTradePriceBucket -> {
            buffer.append(",\"").append(aTradePriceBucket.getName()).append("\"");
        });
        return buffer.toString();
    }

}
