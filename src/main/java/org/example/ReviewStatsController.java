package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dto.ProductMonthlyStatsDto;
import org.example.service.ReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewStatsController {

    private final ReviewService reviewService;

    public ReviewStatsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(value = "/stats/monthly.csv", produces = "text/csv")
    public ResponseEntity<byte[]> getMonthlyPositiveReviewsStatsCsv() {
        List<ProductMonthlyStatsDto> stats = reviewService.getMonthlyReviewsStats();

        String header = String.join(",",
                "productId",
                "productTitle",
                "productUrl",
                "year",
                "month",
                "positiveReviewsCount",
                "totalPositiveReviews");

        String body = stats.stream()
                .map(s -> String.join(",",
                        String.valueOf(s.getProductId()),
                        escapeCsv(s.getProductTitle()),
                        escapeCsv(s.getProductUrl()),
                        String.valueOf(s.getYear()),
                        String.valueOf(s.getMonth()),
                        String.valueOf(s.getPositiveReviewsCount()),
                        String.valueOf(s.getTotalPositiveReviews())
                ))
                .collect(Collectors.joining("\n"));

        String csv = header + "\n" + body + (body.isEmpty() ? "" : "\n");
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_positive_reviews_stats.csv");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/stats/monthly.xlsx")
    public ResponseEntity<byte[]> getMonthlyPositiveReviewsStatsXlsx() {
        List<ProductMonthlyStatsDto> stats = reviewService.getMonthlyReviewsStats();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("MonthlyStats");

            // Header style: bold + green fill (#8bc34a)
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

            // Data row base styles: odd (blueish #d0e0e3) and even (white-ish #eef7e3)
            CellStyle oddRowBase = workbook.createCellStyle();
            if (oddRowBase instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
                org.apache.poi.xssf.usermodel.XSSFCellStyle xs = (org.apache.poi.xssf.usermodel.XSSFCellStyle) oddRowBase;
                xs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                xs.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{
                        (byte) 0xd0, (byte) 0xe0, (byte) 0xe3
                }, null));
            }
            CellStyle evenRowBase = workbook.createCellStyle();
            if (evenRowBase instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
                org.apache.poi.xssf.usermodel.XSSFCellStyle xs = (org.apache.poi.xssf.usermodel.XSSFCellStyle) evenRowBase;
                xs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                xs.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{
                        (byte) 0xee, (byte) 0xf7, (byte) 0xe3
                }, null));
            }

            String[] columns = new String[]{
                    "productId",
                    "productTitle",
                    "productUrl",
                    "year",
                    "month",
                    "positiveReviewsCount",
                    "totalPositiveReviews"
            };

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows with styling and borders logic translated from Python
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

                boolean isOdd = (dataRowIndex % 2 == 1); // Python: odd rows -> blue (#d0e0e3)
                CellStyle baseStyle = isOdd ? oddRowBase : evenRowBase;
                // Apply base style to all cells in the row
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row.getCell(i);
                    cell.setCellStyle(baseStyle);
                }

                Long currentProductId = s.getProductId();
                boolean groupStart = (prevProductId == null) || (currentProductId == null ? prevProductId != null : !currentProductId.equals(prevProductId));

                if (groupStart) {
                    // Add top border to all cells in this row
                    for (int i = 0; i < columns.length; i++) {
                        Cell cell = row.getCell(i);
                        CellStyle newStyle = workbook.createCellStyle();
                        newStyle.cloneStyleFrom(cell.getCellStyle());
                        newStyle.setBorderTop(BorderStyle.THIN);
                        cell.setCellStyle(newStyle);
                    }
                    // Add bottom border to previous row's first column cell
                    if (prevRow != null) {
                        Cell prevFirst = prevRow.getCell(0);
                        if (prevFirst != null) {
                            CellStyle prevStyle = prevFirst.getCellStyle();
                            CellStyle mod = workbook.createCellStyle();
                            mod.cloneStyleFrom(prevStyle);
                            mod.setBorderBottom(BorderStyle.THIN);
                            prevFirst.setCellStyle(mod);
                        }
                    }
                }

                prevProductId = currentProductId;
                prevRow = row;
                dataRowIndex++;
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Conditional formatting: yellow fill for F2:F{last} between 6 and 9
            if (!stats.isEmpty()) {
                SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
                ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN, "6", "9");
                PatternFormatting pf = rule.createPatternFormatting();
                pf.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                pf.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                org.apache.poi.ss.util.CellRangeAddress[] regions = new org.apache.poi.ss.util.CellRangeAddress[]{
                        org.apache.poi.ss.util.CellRangeAddress.valueOf("F2:F" + (stats.size() + 1))
                };
                sheetCF.addConditionalFormatting(regions, rule);
            }

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_positive_reviews_stats.xlsx");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        boolean containsSpecial = value.contains(",") || value.contains("\n") || value.contains("\r") || value.contains("\"");
        String escaped = value.replace("\"", "\"\"");
        if (containsSpecial) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
