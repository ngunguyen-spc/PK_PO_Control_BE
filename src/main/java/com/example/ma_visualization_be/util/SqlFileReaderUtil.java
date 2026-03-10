package com.example.ma_visualization_be.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SqlFileReaderUtil {
    public static String readSqlFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
