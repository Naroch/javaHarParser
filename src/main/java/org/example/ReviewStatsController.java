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

            // Header style: bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

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

            // Data rows
            int rowIdx = 1;
            for (ProductMonthlyStatsDto s : stats) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(s.getProductId());
                row.createCell(col++).setCellValue(s.getProductTitle() == null ? "" : s.getProductTitle());
                row.createCell(col++).setCellValue(s.getProductUrl() == null ? "" : s.getProductUrl());
                row.createCell(col++).setCellValue(s.getYear());
                row.createCell(col++).setCellValue(s.getMonth());
                row.createCell(col++).setCellValue(s.getPositiveReviewsCount());
                row.createCell(col++).setCellValue(s.getTotalPositiveReviews());
            }

            // Autosize columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
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
