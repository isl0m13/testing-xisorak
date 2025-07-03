package com.example.appgidritexmonitoring.util;

import com.example.appgidritexmonitoring.payload.HydrologicalStationMeasurementDTO;
import com.example.appgidritexmonitoring.payload.WaterFlowMeterMeasurementDTO;
import com.example.appgidritexmonitoring.payload.waterlevelgauge.WaterLevelGaugeMeasurementDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerDataDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersDataByDateDTO;
import com.example.appgidritexmonitoring.payload.spillway.SpillwayWaterFlowDTO;
import com.example.appgidritexmonitoring.payload.spillway.SpillwaysDataByDate;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExcelGenerator {
    private final static String CALIBRI_FF = "Calibri";

    public static Resource generateFileForPiezometers(List<PiezometersDataByDateDTO> dtos, List<Integer> ordinalsOfPiz) {
        if (!dtos.isEmpty() && !ordinalsOfPiz.isEmpty()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("sheet");

                //creating styles for upper column names and values
                XSSFCellStyle cellStyleForColumnName = getCellStyle(workbook, true);
                XSSFCellStyle cellStyleForValues = getCellStyle(workbook, false);

                //creating first row and first two columns(N#, Date)
                XSSFRow row = createFirstTwoDefaultColumns(sheet, cellStyleForColumnName);

                XSSFRow row1 = sheet.createRow(1);

                byte firstCol = 2;
                byte lastCol = 3;
                for (Integer integer : ordinalsOfPiz) {
                    XSSFCell nameFirst = row.createCell(firstCol);
                    XSSFCell nameSecond = row.createCell(lastCol);
                    XSSFCell pressureName = row1.createCell(firstCol);
                    XSSFCell tempName = row1.createCell(lastCol);
                    nameFirst.setCellValue("N " + integer + " пьезометри маълумотлари");
                    pressureName.setCellValue("Сув сатхи м");
                    tempName.setCellValue("Ҳарорати °С");
                    nameFirst.setCellStyle(cellStyleForColumnName);
                    nameSecond.setCellStyle(cellStyleForColumnName);
                    pressureName.setCellStyle(cellStyleForColumnName);
                    tempName.setCellStyle(cellStyleForColumnName);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, lastCol));
                    sheet.setColumnWidth(firstCol, 23 * 256);
                    sheet.setColumnWidth(lastCol, 23 * 256);
                    firstCol += 2;
                    lastCol += 2;
                }

                short colFirst = 2;
                short colLast = 3;
                for (int i = 0; i < dtos.size(); i++) {
                    XSSFRow dataRow = sheet.createRow(i + 2);
                    PiezometersDataByDateDTO dto = dtos.get(i);
                    XSSFCell order = dataRow.createCell(0);
                    order.setCellValue(i + 1 + ".");
                    XSSFCell date = dataRow.createCell(1);
                    date.setCellValue(CommonUtils.parseLocalDateTimeToString(dto.getDate()));
                    List<PiezometerDataDTO> piezometerIndications = dto.getPiezometerIndications();
                    for (Integer ordinal: ordinalsOfPiz) {
                        XSSFCell pressureCell = dataRow.createCell(colFirst);
                        XSSFCell tempCell = dataRow.createCell(colLast);
                        Optional<PiezometerDataDTO> optionalPiezometerDataDTO = piezometerIndications
                                .stream()
                                .filter(piezometerDataDTO -> piezometerDataDTO.getOrdinal().equals(ordinal))
                                .findFirst();

                        if (optionalPiezometerDataDTO.isPresent()){
                            PiezometerDataDTO piezometerIndication  = optionalPiezometerDataDTO.get();
                            pressureCell.setCellValue(piezometerIndication.getHydroPressure());
                            tempCell.setCellValue(piezometerIndication.getTemperature());
                        }else {
                            pressureCell.setCellValue("-");
                            tempCell.setCellValue("-");
                        }

                        pressureCell.setCellStyle(cellStyleForValues);
                        tempCell.setCellStyle(cellStyleForValues);

                        colFirst += 2;
                        colLast += 2;
                    }
                    colFirst = 2;
                    colLast = 3;

                    order.setCellStyle(cellStyleForValues);
                    date.setCellStyle(cellStyleForValues);

                }

                //saving file and returning
                return saveWorkbookAsExcelAndGetResource(workbook);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static Resource generateFileForWaterLevelGauges(List<WaterLevelGaugeMeasurementDTO> data) {
        if (!data.isEmpty()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("sheet");

                //creating styles for upper column names and values
                XSSFCellStyle cellStyleForColumnName = getCellStyle(workbook, true);
                XSSFCellStyle cellStyleForValues = getCellStyle(workbook, false);

                //creating first row and first two columns(N#, Date)
                XSSFRow row = createFirstTwoDefaultColumns(sheet, cellStyleForColumnName);

                //creating third and fourth columns and setting up
                XSSFRow row1 = sheet.createRow(1);
                XSSFCell nameFirst = row.createCell(2);
                XSSFCell nameSecond = row.createCell(3);
                XSSFCell pressureNameFirst = row1.createCell(2);
                XSSFCell pressureNameSecond = row1.createCell(3);
                nameFirst.setCellValue("ЮБСС маълумотлари");
                pressureNameFirst.setCellValue("Сув сатхи м");
                nameFirst.setCellStyle(cellStyleForColumnName);
                nameSecond.setCellStyle(cellStyleForColumnName);
                pressureNameFirst.setCellStyle(cellStyleForColumnName);
                pressureNameSecond.setCellStyle(cellStyleForColumnName);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
                sheet.setColumnWidth(2, 23 * 256);
                sheet.setColumnWidth(3, 23 * 256);

                //creating rows with values
                short numberOfRow = 2;
                for (int i = 0; i < data.size(); i++) {
                    WaterLevelGaugeMeasurementDTO dto = data.get(i);
                    XSSFRow valueRow = sheet.createRow(numberOfRow);
                    XSSFCell ordinalCol = valueRow.createCell(0);
                    XSSFCell dateCol = valueRow.createCell(1);
                    XSSFCell pressureColOne = valueRow.createCell(2);
                    XSSFCell pressureColTwo = valueRow.createCell(3);

                    ordinalCol.setCellValue(i + 1 + ".");
                    dateCol.setCellValue(CommonUtils.parseLocalDateTimeToString(dto.getDate()));
                    pressureColOne.setCellValue(dto.getHydroPressure());

                    ordinalCol.setCellStyle(cellStyleForValues);
                    dateCol.setCellStyle(cellStyleForValues);
                    pressureColOne.setCellStyle(cellStyleForValues);
                    pressureColTwo.setCellStyle(cellStyleForValues);

                    sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 2, 3));
                    numberOfRow++;
                }

                //saving file and returning
                return saveWorkbookAsExcelAndGetResource(workbook);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Resource generateFileForWaterFlowMeters(List<WaterFlowMeterMeasurementDTO> data) {
        if (!data.isEmpty()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("sheet");

                //creating styles for upper column names and values
                XSSFCellStyle cellStyleForColumnName = getCellStyle(workbook, true);
                XSSFCellStyle cellStyleForValues = getCellStyle(workbook, false);

                //creating first row and first two columns(N#, Date)
                XSSFRow row = createFirstTwoDefaultColumns(sheet, cellStyleForColumnName);

                //creating third and fourth columns and setting up
                XSSFRow row1 = sheet.createRow(1);
                XSSFCell nameFirst = row.createCell(2);
                XSSFCell nameSecond = row.createCell(3);
                XSSFCell pressureNameFirst = row1.createCell(2);
                XSSFCell tempName = row1.createCell(3);

                nameFirst.setCellValue("Ултра товушли сув сарфини ўлчагич маълумотлари");
                pressureNameFirst.setCellValue("Сув сатхи м");
                tempName.setCellValue("Ҳаво харорати °С");

                nameFirst.setCellStyle(cellStyleForColumnName);
                nameSecond.setCellStyle(cellStyleForColumnName);
                pressureNameFirst.setCellStyle(cellStyleForColumnName);
                tempName.setCellStyle(cellStyleForColumnName);

                sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));
                sheet.setColumnWidth(2, 35 * 256);
                sheet.setColumnWidth(3, 35 * 256);

                //creating rows with values
                short numberOfRow = 2;
                for (int i = 0; i < data.size(); i++) {
                    WaterFlowMeterMeasurementDTO dto = data.get(i);
                    XSSFRow valueRow = sheet.createRow(numberOfRow);
                    XSSFCell ordinalCol = valueRow.createCell(0);
                    XSSFCell dateCol = valueRow.createCell(1);
                    XSSFCell pressureCol = valueRow.createCell(2);
                    XSSFCell tempCol = valueRow.createCell(3);

                    ordinalCol.setCellValue(i + 1 + ".");
                    dateCol.setCellValue(CommonUtils.parseLocalDateTimeToString(dto.getDate()));
                    pressureCol.setCellValue(dto.getWaterFlow());
                    tempCol.setCellValue(dto.getTemperature());

                    ordinalCol.setCellStyle(cellStyleForValues);
                    dateCol.setCellStyle(cellStyleForValues);
                    pressureCol.setCellStyle(cellStyleForValues);
                    tempCol.setCellStyle(cellStyleForValues);

                    numberOfRow++;
                }

                //saving file and returning
                return saveWorkbookAsExcelAndGetResource(workbook);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Resource generateFileForHydrologicalStations(List<String> hydrologicalStations,
                                                               List<HydrologicalStationMeasurementDTO> data) {
        if (!data.isEmpty() && !hydrologicalStations.isEmpty()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("sheet");

                //creating styles for upper column names and values
                XSSFCellStyle cellStyleForColumnName = getCellStyle(workbook, true);
                XSSFCellStyle cellStyleForValues = getCellStyle(workbook, false);

                //creating first row and first two columns(N#, Date)
                XSSFRow row = createFirstTwoDefaultColumns(sheet, cellStyleForColumnName);

                //creating names of devices with indication types
                XSSFRow row1 = sheet.createRow(1);
                short nameColIndex = 2;
                for (String name : hydrologicalStations) {
                    XSSFCell nameFirst = row.createCell(nameColIndex);
                    XSSFCell nameSecond = row.createCell(nameColIndex + 1);
                    XSSFCell pressureNameFirst = row1.createCell(nameColIndex);
                    XSSFCell tempName = row1.createCell(nameColIndex + 1);

                    nameFirst.setCellValue(name);
                    pressureNameFirst.setCellValue("Сув сатхи м");
                    tempName.setCellValue("Ҳаво харорати °С");

                    nameFirst.setCellStyle(cellStyleForColumnName);
                    nameSecond.setCellStyle(cellStyleForColumnName);
                    pressureNameFirst.setCellStyle(cellStyleForColumnName);
                    tempName.setCellStyle(cellStyleForColumnName);

                    sheet.addMergedRegion(new CellRangeAddress(0, 0, nameColIndex, nameColIndex + 1));
                    sheet.setColumnWidth(nameColIndex, 35 * 256);
                    sheet.setColumnWidth(nameColIndex + 1, 35 * 256);

                    nameColIndex += 2;
                }

                //creating values
                short numberOfRow = 2;
                for (int i = 0; i < data.size(); i++) {
                    HydrologicalStationMeasurementDTO dto = data.get(i);
                    XSSFRow valueRow = sheet.createRow(numberOfRow);
                    XSSFCell ordinalCol = valueRow.createCell(0);
                    XSSFCell dateCol = valueRow.createCell(1);

                    short valColIndexNum = 2;

                    for (HydrologicalStationMeasurementDTO indication : dto.getIndications()) {
                        XSSFCell pressureCol = valueRow.createCell(valColIndexNum);
                        XSSFCell tempCol = valueRow.createCell(valColIndexNum + 1);

                        pressureCol.setCellValue(indication.getWaterFlow());
                        tempCol.setCellValue(indication.getTemperature());

                        pressureCol.setCellStyle(cellStyleForValues);
                        tempCol.setCellStyle(cellStyleForValues);

                        valColIndexNum += 2;
                    }

                    ordinalCol.setCellValue(i + 1 + ".");
                    dateCol.setCellValue(CommonUtils.parseLocalDateTimeToString(dto.getDate()));

                    ordinalCol.setCellStyle(cellStyleForValues);
                    dateCol.setCellStyle(cellStyleForValues);

                    numberOfRow++;
                }

                //saving file and returning
                return saveWorkbookAsExcelAndGetResource(workbook);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Resource generateFileForSpillways(List<String> namesOfSpillways, List<SpillwaysDataByDate> data) {
        if (!data.isEmpty() && !namesOfSpillways.isEmpty()) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("sheet");

                //creating styles for upper column names and values
                XSSFCellStyle cellStyleForColumnName = getCellStyle(workbook, true);
                XSSFCellStyle cellStyleForValues = getCellStyle(workbook, false);

                //creating first row and first two columns(N#, Date)
                XSSFRow row = createFirstTwoDefaultColumns(sheet, cellStyleForColumnName);

                //creating names of devices with indication types
                XSSFRow row1 = sheet.createRow(1);
                short nameColIndex = 2;
                for (String name : namesOfSpillways) {
                    XSSFCell nameFirst = row.createCell(nameColIndex);
                    XSSFCell nameSecond = row.createCell(nameColIndex + 1);
                    XSSFCell waterFlowColName = row1.createCell(nameColIndex);
                    XSSFCell pressureNameSecond = row1.createCell(nameColIndex + 1);

                    nameFirst.setCellValue(name);
                    waterFlowColName.setCellValue("Сув сарфи л/с");

                    nameFirst.setCellStyle(cellStyleForColumnName);
                    nameSecond.setCellStyle(cellStyleForColumnName);
                    waterFlowColName.setCellStyle(cellStyleForColumnName);
                    pressureNameSecond.setCellStyle(cellStyleForColumnName);

                    sheet.addMergedRegion(new CellRangeAddress(0, 0, nameColIndex, nameColIndex + 1));
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, nameColIndex, nameColIndex + 1));
                    sheet.setColumnWidth(nameColIndex, 35 * 256);
                    sheet.setColumnWidth(nameColIndex + 1, 35 * 256);

                    nameColIndex += 2;
                }

                //creating values
                short numberOfRow = 2;
                for (int i = 0; i < data.size(); i++) {
                    SpillwaysDataByDate dto = data.get(i);
                    XSSFRow valueRow = sheet.createRow(numberOfRow);
                    XSSFCell ordinalCol = valueRow.createCell(0);
                    XSSFCell dateCol = valueRow.createCell(1);

                    short valColIndexNum = 2;

                    for (SpillwayWaterFlowDTO indication : dto.getIndications()) {
                        XSSFCell waterFLowColOne = valueRow.createCell(valColIndexNum);
                        XSSFCell waterFLowColTwo = valueRow.createCell(valColIndexNum + 1);

                        Double waterFlow = indication.getWaterFlow();
                        if (!waterFlow.isNaN())
                            waterFLowColOne.setCellValue(waterFlow);
                        else
                            waterFLowColOne.setCellValue("-");

                        waterFLowColOne.setCellStyle(cellStyleForValues);
                        waterFLowColTwo.setCellStyle(cellStyleForValues);

                        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, valColIndexNum, valColIndexNum + 1));

                        valColIndexNum += 2;
                    }

                    ordinalCol.setCellValue(i + 1 + ".");
                    dateCol.setCellValue(CommonUtils.parseLocalDateTimeToString(dto.getDate()));

                    ordinalCol.setCellStyle(cellStyleForValues);
                    dateCol.setCellStyle(cellStyleForValues);

                    numberOfRow++;
                }

                //saving file and returning
                return saveWorkbookAsExcelAndGetResource(workbook);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static XSSFRow createFirstTwoDefaultColumns(XSSFSheet sheet, XSSFCellStyle cellStyle) {
        XSSFRow row = sheet.createRow(0);
        row.setHeightInPoints((short) 100);
        XSSFCell ordinalColm = row.createCell(0);
        XSSFCell dateColm = row.createCell(1);

        ordinalColm.setCellValue("Т.р");
        dateColm.setCellValue("Ўлчов олинган сана");
        ordinalColm.setCellStyle(cellStyle);
        dateColm.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet.setColumnWidth(1, 30 * 256);
        return row;
    }

    private static Resource saveWorkbookAsExcelAndGetResource(XSSFWorkbook workbook) {
        File file = new File(UUID.randomUUID() + ".xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
            return new UrlResource(file.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static XSSFCellStyle getCellStyle(XSSFWorkbook workbook, boolean isColumnName) {
        XSSFCellStyle cellStyle = createCellStyle(workbook);

        Font font = workbook.createFont();
        font.setFontName(CALIBRI_FF);

        if (isColumnName) {
            font.setBold(true);
            font.setFontHeightInPoints((short) 16);
        } else {
            font.setFontHeightInPoints((short) 14);
        }

        cellStyle.setFont(font);
        return cellStyle;
    }

    private static XSSFCellStyle createCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }


}
