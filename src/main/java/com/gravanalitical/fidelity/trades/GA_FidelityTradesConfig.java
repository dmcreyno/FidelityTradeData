package com.gravanalitical.fidelity.trades;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Reads the "fidelity.properties file referenced on the command line
 * using the -D option where the base directory is defined.
 * -Dcom.ga.fidelity.trades.home=/users/mary/trade_data/MSFT
 */
class GA_FidelityTradesConfig {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.GA_FidelityTradesConfig");

    private static final String HOME_KEY                      = "com.ga.fidelity.trades.home";
    private static final String HEADER_SKIP_LINE_COUNT        = "com.ga.fidelity.trades.skip.header";
    private static final String TICKER                        = "com.ga.fidelity.trades.ticker";
    private static final String TRADES_BUCKET                 = "com.ga.fidelity.trades.bucket";
    private static final String BUCKET_NAMES                  = "com.ga.fidelity.trades.bucket.names";//=0001,0002
    private static final String BUCKET_MINS                   = "com.ga.fidelity.trades.bucket.mins";//=0.00001,0.00019
    private static final String BUCKET_MAXS                   = "com.ga.fidelity.trades.bucket.maxs";//=0.00020,0.000299
    private static final String BUCKET_LOGIC                  = "com.ga.fidelity.trades.bucket.logx";//=INCLUSIVE,INCLUSIVE


    private static final GA_FidelityTradesConfig _instance = new GA_FidelityTradesConfig();

    private static String baseDir;
    private static String fileSeparator;
    private static Configuration config;
    private List<Bucket> bucketList = new ArrayList<>();

    private GA_FidelityTradesConfig() {
        baseDir = System.getProperty(HOME_KEY);
        fileSeparator = System.getProperty("file.separator");
        if(log.isDebugEnabled()) {
            log.debug("BASE_DIR: " + baseDir);
            log.debug("FILE SEP: " + fileSeparator);
        }

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(baseDir+fileSeparator+"fidelity.properties")
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));

        try
        {
            config = builder.getConfiguration();
            List bucketNames = config.getList(BUCKET_NAMES);
            List bucketMaxs = config.getList(BUCKET_MAXS);
            List bucketMins = config.getList(BUCKET_MINS);
            List bucketLogxes = config.getList(BUCKET_LOGIC);
            int bucketListSize = bucketNames.size();
            for(int i = 0; i < bucketListSize; i++) {
                // Bucket(String name, BigDecimal min, BigDecimal max, COMPARISON_LOGIC compLogic)
                String bucketName = (String) bucketNames.get(i);
                BigDecimal bucketMin = new BigDecimal((String)bucketMins.get(i));
                BigDecimal bucketMax = new BigDecimal((String)bucketMaxs.get(i));
                Bucket.COMPARISON_LOGIC bucketLogx = Bucket.COMPARISON_LOGIC.valueOf((String)bucketLogxes.get(i));
                Bucket aBucket = new Bucket(bucketName,bucketMin,bucketMax,bucketLogx);
                log.info("Bucket: {}", aBucket);
                this.bucketList.add(aBucket);
            }

        }
        catch(ConfigurationException cex)
        {
            log.error("configuration failed.",cex);
            System.exit(-1);
        }
    }

    static GA_FidelityTradesConfig getInstance() {
        return _instance;
    }

    String getHomeDir() {
        return baseDir;
    }

    int getHeaderSkipLineCount() {
        return config.getInt(HEADER_SKIP_LINE_COUNT);
    }

    String getTicker() {
        return config.getString(TICKER);
    }

    String getInputDir() {
        return baseDir+fileSeparator+"input";
    }

    public static void main(String[] args) {
        GA_FidelityTradesConfig local_config = GA_FidelityTradesConfig.getInstance();
        System.out.println(config);
    }

    public List<Bucket> getBuckets() {
        return this.bucketList;
    }
}
