package com.gravanalitical.fidelity.trades;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class ApacheTests {
    @Test
    public void TestFileUtils() {
        String path = "Z:/David/StockAnalysis";
        IOFileFilter fileFilter;
        try {
            File dir = FileUtils.getFile(path);
            Collection<File> dirContent = FileUtils.listFilesAndDirs(dir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            dirContent.iterator().forEachRemaining(pFile -> System.out.println("Found: " + pFile.getAbsolutePath()));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void TestFile() {
        String path = "Z:/David/StockAnalysis";
        IOFileFilter fileFilter;
        try {
            File dir = FileUtils.getFile(path);
            File[] files = dir.listFiles();
            for(File file : files) {
                if(file.isDirectory()) System.out.println("Found: " + file);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
