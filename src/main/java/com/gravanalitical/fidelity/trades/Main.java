package com.gravanalitical.fidelity.trades;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class Main {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.Main");
    private static final String OUT_HEADER = "Date" +
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
/*
        buf.append(dateStr).append(",")
                .append(getAveragePrice()).append(",")
                .append(getVolume()).append(",")
                .append(getBuyVolume()).append(",")
                .append(getSellVolume()).append(",")
                .append(getUnknownVolume()).append(",")
                .append(getDollarVolume()).append(",")
                .append(getBuyDollarVolume()).append(",")
                .append(getSellDollarVolume()).append(",")
                .append(getUnknownDollarVolume()).append(",")
                .append(getPctBuyVol()).append(",")
                .append(getPctSellVol()).append(",")
                .append(getPctUnknownVol()).append(",")
                .append(getPctBuyDolVol()).append(",")
                .append(getPctSellDolVol()).append(",")
                .append(getPctUnknownDolVol()).append(",");

 */

    public Main() {

    }

    public static void main(String[] args) {
        log.info("starting . . .");
        Main app = new Main();
        app.processDirectory();
    }

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

}
