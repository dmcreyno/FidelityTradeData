package com.gravanalitical.fidelity.trades;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GA_FidelityTradesConfig {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.GA_FidelityTradesConfig");

    private static final String GA_FIDELITY_TRADES_HOME_KEY   = "com.ga.fidelity.trades.home";
    private static final String HEADER_SKIP_LINE_COUNT        = "com.ga.fidelity.trades.skip.header";
    private static final String COM_GA_FIDELITY_TRADES_TICKER = "com.ga.fidelity.trades.ticker";

    private static final GA_FidelityTradesConfig _instance = new GA_FidelityTradesConfig();

    private String baseDir;
    private String fileSeparator;
    private Configuration config;

    private GA_FidelityTradesConfig() {
        baseDir = System.getProperty(GA_FIDELITY_TRADES_HOME_KEY);
        fileSeparator = System.getProperty("file.separator");
        if(log.isDebugEnabled()) {
            log.debug("BASE_DIR: " + baseDir);
            log.debug("FILE SEP: " + fileSeparator);
        }

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(baseDir+fileSeparator+"fidelity.properties"));
        try
        {
            config = builder.getConfiguration();
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
        return config.getString(COM_GA_FIDELITY_TRADES_TICKER);
    }

    String getInputDir() {
        return baseDir+fileSeparator+"input";
    }
}
