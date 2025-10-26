package org.example.service;

import java.io.File;

public class FileLoader {
    private static final String DIRECTORY_PATH = "harfiles";
    private static final String TEST_FILE = "testFile.har";
    private static final File directory = new File(DIRECTORY_PATH);

    public static File[] loadFiles() {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".har"));
            System.out.println("Znaleziono plików: " + (files != null ? files.length : 0));

            if (files != null) {
                return files;
            } else {
                throw new RuntimeException("Nie znaleziono plików HAR w podanym katalogu.");
            }
        } else {
            throw new RuntimeException("Podana ścieżka nie jest katalogiem.");
        }
    }

    public static File loadTestFile()  {
        if (directory.isDirectory()) {
            File testFile = new File(directory.getAbsolutePath() + "/" + TEST_FILE);
            return testFile;
        } else {
            throw new RuntimeException("Podana ścieżka nie jest katalogiem.");
        }
    }
}
