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

package com.gravanalitical.fidelity.trades;

import com.gravanalitical.fidelity.trades.config.GA_FidelityTradesConfig;
import com.gravanalitical.fidelity.trades.format.TradeDayFormatFactory;
import com.gravanalitical.fidelity.trades.format.TradeDayPresentation;
import com.gravanalitical.fidelity.trades.format.TradeMonthAsTabular;
import com.gravanalitical.locale.DisplayKeys;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * Program to read a collection of Fidelity trades exported from ActiveTraderPro as CSV files.
 * Uses a "base" directory structure to hold the input files. The directory referenced
 * by -Dom.ga.fidelity.trades.home is assumed to have a collection of ticker directories each of
 * which has an input folder named, "input" containing CSV files named with the
 * date of the day the trades were executed.
 *
 * Reads the "fidelity.properties file referenced on the command line
 * using the -D option where the base directory is defined. It is assumed the directory will have
 * a sub-folder named <i>input</i> when the CSV files downloaded from Fidelity will be found.
 *
 * The code will travers all subdirectories and pick up any file with a "csv" extension inside an "input" directory.
 * <b>Example</b><br>
 * -Dcom.ga.fidelity.trades.home=/users/mary/trade_data
 *
 * The code will process only the passed subdirectories and pick up any file with a "csv" extension inside an "input"
 * directory.
 * <b>Example</b><br>
 * -Dcom.ga.fidelity.trades.home=/users/mary/trade_data GWRE
 *
 * The system uses the fidelity.properties file, also located in the base/[ticker] dir, to contain the ticker
 * symbol which will be used to generate the output file name CSV file. This way, the user may keep their own
 * directory naming structure - the program will not use the directory name as the ticker name.
 */
public class Main {
    private static final Logger log = LogManager.getLogger("fidelity.trades.Main");
    private int fileCounter = 0;
    private TradeMonth monthly;
    private static boolean HAS_ARGS = false;
    private static TreeSet<String> TICKER_ARGS = new TreeSet();

    @SuppressWarnings({"unused"})
    public Main(String[] args) {
        if(log.isInfoEnabled()) {
            for (String arg: args) {
                log.info("Main(String[]) - arg: {}", arg);
            }
        }
        HAS_ARGS = args.length > 0;
        if(HAS_ARGS) {
            for(String arg:args) {
                TICKER_ARGS.add(arg);
            }
        }
    }

    public static void main(String[] args) {
        log.info(DisplayKeys.get(DisplayKeys.STARTUP));
        Main app = new Main(args);
        try {
            String baseDir = System.getProperty(GA_FidelityTradesConfig.PropertyConstants.HOME_KEY);
            File dir = FileUtils.getFile(baseDir);
            File[] files = dir.listFiles();
            if(null == files) {
                log.error("No directories to process.");
            } else {
                Arrays.stream(files).filter(File::isDirectory).forEach(file -> {
                    if(HAS_ARGS && !TICKER_ARGS.contains(file.getName())) {
                        log.debug(" main(String[]) skipping {}", file);
                    } else {
                        String baseDirName = file.getAbsolutePath();
                        GA_FidelityTradesConfig.init(baseDirName);
                        ThreadContext.put("ticker", file.getName());
                        log.info(DisplayKeys.get(DisplayKeys.PROCESSING_FILE), baseDirName);
                        app.processDirectory(baseDirName);
                        ThreadContext.pop();
                    }
                });
            }
        } catch(Exception ex) {
            log.error(DisplayKeys.get(DisplayKeys.ERROR), ex);
        }

    }

    /**
     * Traverses all directories and processes the inputs for each. Each directory is a ticker symbol
     * and the program expects to find a "fidelity.properties" within each.
     */
    private void processDirectory(String baseDireName) {
        GA_FidelityTradesConfig config = GA_FidelityTradesConfig.init(baseDireName);
        monthly = new TradeMonth(config);
        String OUT_HEADER = config.getOutputHeader();
        String outStr = config.getHomeDir();
        String ticker = config.getTicker();
        File outfile;
        String inDirStr;
        Collection<File> inputList;
        TreeSet<File> sortedInputList;

        TradeMonthAsTabular monthFormatter = new TradeMonthAsTabular();

        outfile = new File(outStr+System.getProperty("file.separator") + ticker + "." + GA_FidelityTradesConfig.CSV_FILE_EXTENSION);
        log.debug(DisplayKeys.get(DisplayKeys.PROCESSING_OUTPUT_FILE), outfile.getAbsolutePath());

        try (   FileWriter outFileWriter = new FileWriter(outfile);
                PrintWriter pw = new PrintWriter(outFileWriter);
                FileWriter summaryFileWriter = new FileWriter(baseDireName + "/summary.txt");
                PrintWriter summaryPrintWriter = new PrintWriter(summaryFileWriter)
             ) {

            pw.println(OUT_HEADER);

            inDirStr = config.getInputDir();
            inputList = FileUtils.listFiles(new File(inDirStr),GA_FidelityTradesConfig.FILE_EXT_FOR_PROCESSING,false);
            sortedInputList = new TreeSet<>(Comparator.comparing(File::getName));

            sortedInputList.addAll(inputList);
            this.fileCounter = 0; // a file per day.

            sortedInputList.forEach( aFile -> {
                String currentFileName = aFile.getName();

                if(currentFileName.startsWith(".")) {
                    log.debug(DisplayKeys.get(DisplayKeys.SKIPPING_HIDDEN_FILE),currentFileName);
                }

                if(log.isDebugEnabled()) {
                    log.debug(DisplayKeys.get(DisplayKeys.PROCESSING_FILE),currentFileName);
                }

                TradeDay aDay = new TradeDay(aFile, config);
                aDay.process();

                if(!aDay.isEmpty()) {
                    updateMonthlyValues(aDay);
                    this.fileCounter++;
                    aDay.setDayOrdinal(this.fileCounter);
                    TradeDayPresentation formatter = TradeDayFormatFactory.getCsvFormatter();
                    String logMessage = formatter.formatTradeDay(aDay);
                    summaryPrintWriter.println(TradeDayFormatFactory.getTabularFormatter().formatTradeDay(aDay));
                    log.info("{}", logMessage);
                    try {
                        aDay.writeSummary(pw, formatter);
                        pw.flush();
                    } catch (Exception e) {
                        log.error(DisplayKeys.get(DisplayKeys.ERROR), e);
                        System.exit(-1);
                    }
                }
            });
            summaryPrintWriter.println(monthFormatter.formatTradeMonth(this.monthly));
        } catch (IOException e) {
            log.error(DisplayKeys.get(DisplayKeys.ERROR_PROC_FILE),outfile.getName(), e);
            System.exit(-1);
        }
    }

    private void updateMonthlyValues(TradeDay pADay) {
        this.monthly.addTotalVolume(pADay.getVolume());
        this.monthly.addTotalBuyVolume(pADay.getBuyVolume());
        this.monthly.addTotalSellVolume(pADay.getSellVolume());
        this.monthly.addTotalUnknownVolume(pADay.getUnknownVolume());

        this.monthly.addTotalDollars(pADay.getDollarVolume());
        this.monthly.addTotalBuyDollars(pADay.getBuyDollarVolume());
        this.monthly.addTotalSellDollars(pADay.getSellDollarVolume());
        this.monthly.addTotalUnknownDollars(pADay.getUnknownDollarVolume());
    }


}
