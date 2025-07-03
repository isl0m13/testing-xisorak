package com.example.appgidritexmonitoring.util;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CSVReader {

    public static Map<String, String> readFirstRecordToMapFromCSV(File file) {
        Map<String, String> results = new HashMap<>();
        if (checkIsCsvFile(file)) {
            CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                 CSVParser parse = csvFormat.parse(bufferedReader)) {
                List<CSVRecord> records = parse.getRecords();
                if (!records.isEmpty()) {
                    List<String> headerNames = parse.getHeaderNames();
                    CSVRecord csvRecord = records.get(0);
                    for (int i = 0; i < csvRecord.size(); i++) {
                        String columnName = headerNames.get(i).trim();
                        String columnValue = csvRecord.get(i).trim();
                        if ((!columnName.isBlank() && !columnName.isEmpty()) && (!columnValue.isBlank() && !columnValue.isEmpty())) {
                            results.put(columnName, columnValue);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static Map<String, String> readSpecificRecordsToMapFromCSV(File file) {
        Map<String, String> readData = new HashMap<>();
        if (checkIsCsvFile(file)) {
            CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                    .build();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                 CSVParser parse = csvFormat.parse(bufferedReader);
            ) {
                List<CSVRecord> records = parse.getRecords();
                if (!records.isEmpty() && records.size() > 9) {
                    List<String> columnNames = records.get(9).stream().toList();
                    List<String> values = records.get(10).stream().toList();
                    for (int i = 0; i < values.size(); i++) {
                        String columnName = columnNames.get(i);
                        String value = values.get(i);
                        if ((Objects.nonNull(columnName) && Objects.nonNull(value)) &&
                                (!columnName.isBlank() && !value.isBlank())) {
                            readData.put(columnName.trim(), value.trim());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return readData;
    }





    public static boolean checkIsCsvFile(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf(".") + 1).equals("csv");
    }
}
