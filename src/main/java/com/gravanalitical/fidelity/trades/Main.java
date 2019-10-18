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

import com.gravanalitical.fidelity.trades.format.TradeDayFormatFactory;
import com.gravanalitical.fidelity.trades.format.TradeDayPresentation;
import com.gravanalitical.fidelity.trades.format.TradeMonthAsTabular;
import com.gravanalitical.locale.DisplayKeys;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * Program to read a collection of Fidelity trades exported from ActiveTraderPro as CSV files.
 * Uses a "base" directory structure to hold the input files. The directory referenced
 * by -Dom.ga.fidelity.trades.home is assumed to hold an input folder named, "input" containing CSV
 * files named with the date of the day the trades were executed.
 * Reads the "fidelity.properties file referenced on the command line
 * using the -D option where the base directory is defined. It is assumed the directory will have
 * a sub-folder named <i>input</i> when the CSV files downloaded from Fidelity will be found.
 * The code will pick up any file with a "csv" extension.
 * <b>Example</b><br>
 * -Dcom.ga.fidelity.trades.home=/users/mary/trade_data/MSFT
 * The system uses the fidelity.properties file, also located in the base dire, to contain the ticker
 * symbol which will be used to generate the output file name CSV file.
 */
public class Main {
    private static final Logger log = LogManager.getLogger("fidelity.trades");
    private static String OUT_HEADER = GA_FidelityTradesConfig.getInstance().getOutputHeader();
    private int fileCounter = 0;
    private TradeMonth monthly = new TradeMonth();

    public Main(String[] args) {

    }

    public static void main(String[] args) {
        log.info(DisplayKeys.get(DisplayKeys.STARTUP));
        Main app = new Main(args);
        app.processDirectory();
    }

    /**
     * Processes all files from the "input" directory ending with "csv"
     */
    private void processDirectory() {
        String outStr = GA_FidelityTradesConfig.getInstance().getHomeDir();
        String ticker = GA_FidelityTradesConfig.getInstance().getTicker();
        File outfile;
        String inDirStr;
        Collection<File> inputList;
        TreeSet<File> sortedInputList;

        TradeMonthAsTabular monthFormatter = new TradeMonthAsTabular();

        outfile = new File(outStr+System.getProperty("file.separator") + ticker + "." + GA_FidelityTradesConfig.CSV_FILE_EXTENSION);
        log.debug(DisplayKeys.get(DisplayKeys.PROCESSING_OUTPUT_FILE), outfile.getAbsolutePath());

        try (   FileWriter outFileWriter = new FileWriter(outfile);
                PrintWriter pw = new PrintWriter(outFileWriter)
             ) {
            pw.println(OUT_HEADER);

            inDirStr = GA_FidelityTradesConfig.getInstance().getInputDir();
            inputList = FileUtils.listFiles(new File(inDirStr),GA_FidelityTradesConfig.FILE_EXT_FOR_PROCESSING,false);
            sortedInputList = new TreeSet<>(Comparator.comparing(File::getName));

            sortedInputList.addAll(inputList);

            sortedInputList.forEach( aFile -> {
                String currentFileName = aFile.getName();

                if(currentFileName.startsWith(".")) {
                    log.debug(DisplayKeys.get(DisplayKeys.SKIPPING_HIDDEN_FILE),currentFileName);
                }

                if(log.isDebugEnabled()) {
                    log.debug(DisplayKeys.get(DisplayKeys.PROCESSING_FILE),currentFileName);
                }

                TradeDay aDay = new TradeDay(aFile);
                aDay.process();

                if(!aDay.isEmpty()) {
                    updateMonthlyValues(aDay);
                    aDay.setDayOrdinal(this.incrementFileCount());
                    TradeDayPresentation formatter = TradeDayFormatFactory.getCsvFormatter();
                    String logMessage = formatter.formatTradeDay(aDay);
                    log.info(GA_FidelityTradesConfig.PRINT_MARKER,"{}", TradeDayFormatFactory.getTabularFormatter().formatTradeDay(aDay));
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
            log.info(GA_FidelityTradesConfig.PRINT_MARKER, monthFormatter.formatTradeMonth(this.monthly));
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

    private int incrementFileCount() {
        return ++fileCounter;
    }

}
