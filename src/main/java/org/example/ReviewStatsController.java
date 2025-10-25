package org.example;

import org.example.dto.ProductMonthlyStatsDto;
import org.example.service.ReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        List<ProductMonthlyStatsDto> stats = reviewService.getMonthlyPositiveReviewsStats();

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
