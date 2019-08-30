package com.gravanalitical.fidelity.trades;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Wrapper around a buffered reader. While there is not much value in wrapping that class
 * this class will skip the summary header info Fidelity puts in their exports.
 */
public class CSVInputReader {
    private static final Logger log = LoggerFactory.getLogger("fidelity.trades.CSVInputReader");

    private BufferedReader reader;
    private File file;
    /**
     * CTOR accepting an instance of a File .
     * @param pFile the file to read from.
     */
    public CSVInputReader(File pFile) {
        file=pFile;
    }

    void initFile() throws IOException {
        reader = new BufferedReader(new FileReader(file));
        // throw away the first 9 lines (currently its 9)
        for (int i = 0; i < GA_FidelityTradesConfig.getInstance().getHeaderSkipLineCount(); i++) {
            reader.readLine();
        }
    }

    String readLine() throws IOException {
        return reader.readLine();
    }

    void close() {
        try {
            reader.close();
        } catch (Exception e) {
            log.error("problem closing reader for {}",file.getAbsolutePath(), e);
        }
    }
}
