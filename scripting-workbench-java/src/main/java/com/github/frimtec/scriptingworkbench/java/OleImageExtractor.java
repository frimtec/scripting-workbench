package com.github.frimtec.scriptingworkbench.java;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.util.OleBlob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility to extract OLE objects (images) from a Microsoft Access database.
 */
public class OleImageExtractor {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: OleImageExtractor <dbPath> <tableName> <columnName>");
            return;
        }

        String dbPath = args[0];
        String tableName = args[1];
        String columnName = args[2];
        Path outputDir = Paths.get("extracted_images");

        try {
            Files.createDirectories(outputDir);
            extractImages(dbPath, tableName, columnName, outputDir);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void extractImages(String dbPath, String tableName, String columnName, Path outputDir) throws IOException {
        try (Database db = new DatabaseBuilder(new File(dbPath)).open()) {
            Table table = db.getTable(tableName);
            if (table == null) {
                throw new IllegalArgumentException("Table not found: " + tableName);
            }

            int count = 0;
            for (Row row : table) {
                Object cellValue = row.get(columnName);
                if (cellValue == null) {
                    continue;
                }
                if (cellValue instanceof byte[] oleBlob) {
                    processOleBlob(oleBlob, outputDir, count++);
                }
            }
            System.out.println("Extracted " + count + " potential images to " + outputDir.toAbsolutePath());
        }
    }

    private static void processOleBlob(byte[] oleBlob, Path outputDir, int index) throws IOException {
        OleBlob blob = OleBlob.Builder.fromInternalData(oleBlob);
        saveToFile(blob, outputDir.resolve("image" + "-" + index + ".bmp"));
    }

    private static void saveToFile(OleBlob in, Path outputPath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
            OleBlob.Content content = in.getContent();
            if (content instanceof OleBlob.SimplePackageContent simplePackage) {
                simplePackage.writeTo(out);
            } else if (content instanceof OleBlob.OtherContent other) {
                other.writeTo(out);
            } else {
                in.writeTo(out);
            }
        }
        System.out.println("Saved: " + outputPath.getFileName());
    }
}
