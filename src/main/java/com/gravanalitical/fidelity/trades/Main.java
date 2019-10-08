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

import com.gravanalitical.fidelity.trades.format.TradeDayFormatFactory;
import com.gravanalitical.fidelity.trades.format.TradeDayPresentation;
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



    public Main() {

    }

    public static void main(String[] args) {
        log.info("starting . . .");
        Main app = new Main();
        app.processDirectory();
    }

    /**
     * Processes all files with ex
     */
    private void processDirectory() {
        String[] csvExt = {"csv"};
        String outStr = GA_FidelityTradesConfig.getInstance().getHomeDir();
        String fileSep = System.getProperty("file.separator");
        String ticker = GA_FidelityTradesConfig.getInstance().getTicker();
        File outfile;
        String inDirStr;
        Collection<File> inputList;
        TreeSet<File> sortedInputList;


        outfile = new File(outStr+fileSep+ticker+".csv");
        log.info("Output file, {}", outfile.getAbsolutePath());

        try {
            FileWriter outFileWriter = new FileWriter(outfile);
            PrintWriter pw = new PrintWriter(outFileWriter);
            pw.println(OUT_HEADER);

            inDirStr = GA_FidelityTradesConfig.getInstance().getInputDir();
            inputList = FileUtils.listFiles(new File(inDirStr),csvExt,false);
            sortedInputList = new TreeSet<>(Comparator.comparing(File::getName));

            sortedInputList.addAll(inputList);

            int fileCount = 0;

            sortedInputList.forEach( aFile -> {
                String currentFileName = aFile.getName();

                if(currentFileName.startsWith(".")) {
                    log.debug("Skipping what appears to be a hidden file, {}",currentFileName);
                }

                if(log.isInfoEnabled()) {
                    log.info("processing: {}",currentFileName);
                }

                TradeDay aDay = new TradeDay(aFile);
                aDay.process();

                if(!aDay.isEmpty()) {
                    aDay.setDayOrdinal(this.incrementFileCount());
                    TradeDayPresentation formatter = TradeDayFormatFactory.getCsvFormatter();
                    String logMessage = formatter.formatTradeDay(aDay);
                    log.info(GA_FidelityTradesConfig.PRINT_MARKER,"{}", TradeDayFormatFactory.getTabularFormatter().formatTradeDay(aDay));
                    log.info("{}", logMessage);
                    try {
                        aDay.writeSummary(pw, formatter);
                        pw.flush();
                    } catch (Exception e) {
                        log.error("problems.", e);
                        System.exit(-1);
                    }
                }
            });
            pw.close();
            outFileWriter.close();
        } catch (IOException e) {
            log.error("cannot open output destination, {}.",outfile.getName(), e);
            System.exit(-1);
        }
    }

    private int incrementFileCount() {
        return ++fileCounter;
    }
}
