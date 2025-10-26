package org.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dto.ProductMonthlyStatsDto;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Service responsible for generating XLSX files for monthly positive review stats.
 */
import org.springframework.stereotype.Service;

@Service
public class MonthlyStatsXlsxService {

    /**
     * Generates an XLSX workbook as bytes for the provided monthly stats.
     *
     * @param stats list of monthly stats to export
     * @return XLSX file content as byte array
     */
    public byte[] generateMonthlyStatsXlsx(List<ProductMonthlyStatsDto> stats) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("MonthlyStats");

            // Prepare styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle oddRowBase = createFillStyle(workbook, (byte) 0xd0, (byte) 0xe0, (byte) 0xe3); // #d0e0e3
            CellStyle evenRowBase = createFillStyle(workbook, (byte) 0xee, (byte) 0xf7, (byte) 0xe3); // #eef7e3

            String[] columns = new String[]{
                    "productId",
                    "productTitle",
                    "productUrl",
                    "year",
                    "month",
                    "positiveReviewsCount",
                    "totalPositiveReviews"
            };

            // Header
            createHeaderRow(sheet, columns, headerStyle);

            // Data rows
            fillDataRows(workbook, sheet, stats, columns, oddRowBase, evenRowBase);

            // Autosize
            autosizeColumns(sheet, columns.length);

            // Conditional formatting for summer months 6-9 on month column (E)
            addMonthBetweenConditionalFormatting(sheet, stats.size());

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate XLSX for monthly stats", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        if (headerStyle instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
            org.apache.poi.xssf.usermodel.XSSFCellStyle xh = (org.apache.poi.xssf.usermodel.XSSFCellStyle) headerStyle;
            xh.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            xh.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{
                    (byte) 0x8b, (byte) 0xc3, (byte) 0x4a
            }, null));
        }
        return headerStyle;
    }

    private CellStyle createFillStyle(Workbook workbook, byte r, byte g, byte b) {
        CellStyle style = workbook.createCellStyle();
        if (style instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
            org.apache.poi.xssf.usermodel.XSSFCellStyle xs = (org.apache.poi.xssf.usermodel.XSSFCellStyle) style;
            xs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            xs.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{r, g, b}, null));
        }
        return style;
    }

    private void createHeaderRow(Sheet sheet, String[] columns, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Workbook workbook,
                              Sheet sheet,
                              List<ProductMonthlyStatsDto> stats,
                              String[] columns,
                              CellStyle oddRowBase,
                              CellStyle evenRowBase) {
        Long prevProductId = null;
        Row prevRow = null;
        int dataRowIndex = 1; // first data row is Excel row 2 (index 1)
        for (ProductMonthlyStatsDto s : stats) {
            Row row = sheet.createRow(dataRowIndex);
            int col = 0;
            Cell c0 = row.createCell(col++); c0.setCellValue(s.getProductId());
            Cell c1 = row.createCell(col++); c1.setCellValue(s.getProductTitle() == null ? "" : s.getProductTitle());
            Cell c2 = row.createCell(col++); c2.setCellValue(s.getProductUrl() == null ? "" : s.getProductUrl());
            Cell c3 = row.createCell(col++); c3.setCellValue(s.getYear());
            Cell c4 = row.createCell(col++); c4.setCellValue(s.getMonth());
            Cell c5 = row.createCell(col++); c5.setCellValue(s.getPositiveReviewsCount());
            Cell c6 = row.createCell(col++); c6.setCellValue(s.getTotalPositiveReviews());

            boolean isOdd = (dataRowIndex % 2 == 1); // odd rows -> blue (#d0e0e3)
            CellStyle baseStyle = isOdd ? oddRowBase : evenRowBase;
            applyRowBaseStyle(row, columns.length, baseStyle);

            Long currentProductId = s.getProductId();
            boolean groupStart = (prevProductId == null) || (currentProductId == null ? prevProductId != null : !currentProductId.equals(prevProductId));

            if (groupStart) {
                addTopBorderToRow(workbook, row, columns.length);
                addBottomBorderToPrevFirstCell(workbook, prevRow);
            }

            prevProductId = currentProductId;
            prevRow = row;
            dataRowIndex++;
        }
    }

    private void applyRowBaseStyle(Row row, int columnCount, CellStyle baseStyle) {
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            cell.setCellStyle(baseStyle);
        }
    }

    private void addTopBorderToRow(Workbook workbook, Row row, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            CellStyle newStyle = workbook.createCellStyle();
            newStyle.cloneStyleFrom(cell.getCellStyle());
            newStyle.setBorderTop(BorderStyle.THIN);
            cell.setCellStyle(newStyle);
        }
    }

    private void addBottomBorderToPrevFirstCell(Workbook workbook, Row prevRow) {
        if (prevRow == null) return;
        Cell prevFirst = prevRow.getCell(0);
        if (prevFirst != null) {
            CellStyle prevStyle = prevFirst.getCellStyle();
            CellStyle mod = workbook.createCellStyle();
            mod.cloneStyleFrom(prevStyle);
            mod.setBorderBottom(BorderStyle.THIN);
            prevFirst.setCellStyle(mod);
        }
    }

    private void autosizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addMonthBetweenConditionalFormatting(Sheet sheet, int statsSize) {
        if (statsSize <= 0) return;
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
        ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "6", "9");
        PatternFormatting pf = rule.createPatternFormatting();
        pf.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
        pf.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
        org.apache.poi.ss.util.CellRangeAddress[] regions = new org.apache.poi.ss.util.CellRangeAddress[]{
                org.apache.poi.ss.util.CellRangeAddress.valueOf("E2:E" + (statsSize + 1))
        };
        sheetCF.addConditionalFormatting(regions, rule);
    }
}
