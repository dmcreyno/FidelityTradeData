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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
