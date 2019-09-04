package com.gravanalitical.fidelity.trades;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Program to read a collection of Fidelity trades exported from ActiveTraderPro as CSV files.
 * Uses a "base" directory structure to hold the input files. The directory referenced
 * by -Dom.ga.fidelity.trades.home is assumed to hold an input folder named, "input" containing CSV
 * files named with the date of the day the trades were executed.
 *
 * The system uses the fidelity.properties file, also located in the base dire, to contain the ticker
 * symbol which will be used to generate the output file name CSV file.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.Main");
    private String OUT_HEADER = "Date" +
            ",Avg Price" +
            ",Volume" +
            ",Buy Vol" +
            ",Sell Vol" +
            ",??? Vol" +
            ",Dollar Vol" +
            ",Buy Dollar Vol" +
            ",Sell Dollar Vol" +
            ",??? Dollar Vol" +
            ",Buy Vol Pct" +
            ",Sell Vol Pct" +
            ",??? Vol Pct" +
            ",Buy Dollar Vol Pct" +
            ",Sell Dollar Vol Pct" +
            ",??? Dollar Vol Pct";


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
            // Fix header for variable number of price buckets
            this.appendBucketNamesToHeader();

            PrintWriter pw = new PrintWriter(new FileWriter(outfile));
            pw.println(OUT_HEADER);

            inDirStr = GA_FidelityTradesConfig.getInstance().getInputDir();
            inputList = FileUtils.listFiles(new File(inDirStr),csvExt,false);
            sortedInputList = new TreeSet<>(Comparator.comparing(File::getName));

            sortedInputList.addAll(inputList);

            sortedInputList.forEach( aFile -> {
                if(log.isInfoEnabled()) {
                    log.info("processing: {}",aFile.getName());
                }
                TradeDay aDay = new TradeDay(aFile);
                aDay.process();
                log.debug("{}",aDay);
                try {
                    aDay.writeSummary(pw);
                    pw.flush();
                } catch (Exception e) {
                    log.error("problems.", e);
                    System.exit(-1);
                }
            });
            pw.close();
        } catch (IOException e) {
            log.error("cannot open output destination, {}.",outfile.getName(), e);
            System.exit(-1);
        }
    }

    private void appendBucketNamesToHeader() {
        GA_FidelityTradesConfig.getInstance().getBuckets().forEach(aTradePriceBucket -> {
            OUT_HEADER = OUT_HEADER + "," + aTradePriceBucket.getName();
        });
    }
}