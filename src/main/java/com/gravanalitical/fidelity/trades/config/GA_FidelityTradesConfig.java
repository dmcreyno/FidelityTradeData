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

package com.gravanalitical.fidelity.trades.config;

import com.gravanalitical.fidelity.trades.TradePriceBucket;
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
 * Reads the configuration, "fidelity.properties" from the base directory passed in the "init" call.
 *
 */
public class GA_FidelityTradesConfig {
    private static final Logger log = LogManager.getLogger("fidelity.trades.GA_FidelityTradesConfig");

    private Locale locale = new Locale("en", "US");
    private ResourceBundle displayKeys = ResourceBundle.getBundle("DisplayKeys", locale);

    public static class PropertyConstants {
        public static final String HOME_KEY                      = "com.ga.fidelity.trades.home";
        static final String OUTPUT_HEADER_LINE_01         = "com.ga.fidelity.trades.output.header1";
        static final String HEADER_SKIP_LINE_COUNT        = "com.ga.fidelity.trades.skip.header";
        static final String TICKER                        = "com.ga.fidelity.trades.ticker";
        static final String DATE_LINE_NUM                 = "com.ga.fidelity.trades.date.line.number"; // Date Line number in the Fidelity CSV export
        static final String BUCKET_NAMES                  = "com.ga.fidelity.trades.bucket.names"; //=0001,0002
        static final String BUCKET_MINS                   = "com.ga.fidelity.trades.bucket.mins";  //=0.00001,0.00019
        static final String BUCKET_MAXS                   = "com.ga.fidelity.trades.bucket.maxs";  //=0.00020,0.000299
        static final String BUCKET_LOGIC                  = "com.ga.fidelity.trades.bucket.logx";  //=INCLUSIVE,INCLUSIVE
        static final String BIG_NUMBER_SCALE              = "com.ga.fidelity.trades.scale";
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

    private static String baseDir;
    private static String fileSeparator;
    private static Configuration config;

    /**
     *
     * @param pBaseDir
     */
    private GA_FidelityTradesConfig(String pBaseDir) {
        baseDir = pBaseDir;
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

    public static GA_FidelityTradesConfig init(String pathName) {
        return new GA_FidelityTradesConfig(pathName);
    }


    public String getHomeDir() {
        return baseDir;
    }

    public int getDateLineNumber() {
        return config.getInt(PropertyConstants.DATE_LINE_NUM, 2);
    }

    /**
     * The input CSV file has some meta data which we will skip. This could vary between CSV providers
     * and that is why the value is configurable.
     * @return
     */
    public int getHeaderSkipLineCount() {
        return config.getInt(PropertyConstants.HEADER_SKIP_LINE_COUNT, 9);
    }

    public int getMathScale() {
        return config.getInt(PropertyConstants.BIG_NUMBER_SCALE, 8);
    }

    public String getTicker() {
        return config.getString(PropertyConstants.TICKER);
    }

    public String getInputDir() {
        return baseDir+fileSeparator+"input";
    }

    /**
     * The header is configured in the properties file.
     * @return
     */
    public String getOutputHeader() {
        // There are two rows to the header with different numbers of columns.
        StringBuilder buffer = new StringBuilder();
        int listSize;
        List<String> columnNames = config.getList(String.class, PropertyConstants.OUTPUT_HEADER_LINE_01);
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


    /**
     * Users can specify how they want trade data partitioned by price ranges. A price range is called
     * a TradeBucket. This function reads the config and creates the list of buckets.
     *
     * Buckets are not required. Deleting the bucket names from the config will cause this function
     * to return an empty list.
     * @return
     */
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
        getBuckets().forEach(aTradePriceBucket -> {
            buffer.append(",\"").append(aTradePriceBucket.getName()).append("\"");
        });
        return buffer.toString();
    }

}
