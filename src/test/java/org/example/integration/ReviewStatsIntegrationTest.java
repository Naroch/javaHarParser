package org.example.integration;

import org.example.Main;
import org.example.dto.ProductMonthlyStatsDto;
import org.example.integration.setup.DatabaseCleanup;
import org.example.integration.setup.TestcontainersConfiguration;
import org.example.model.Product;
import org.example.model.Review;
import org.example.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class ReviewStatsIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setup() {
        databaseCleanup.cleanDatabase();
    }

    @Test
    void should_calculate_monthly_positive_reviews_stats_per_product() {
        // Arrange: create products
        Product p1 = new Product(1L, "Product 1", "http://example.com/p1");
        Product p2 = new Product(2L, "Product 2", "http://example.com/p2");

        // Reviews: p1 has Jan (positive) and Feb (positive + negative), p2 has Jan (positive)
        Review r1 = new Review("r1", "seller", dateOf(2025, 1, 10), dateOf(2025, 1, 10), false, 5, 5, true, List.of(p1));
        Review r2 = new Review("r2", "seller", dateOf(2025, 2, 5), dateOf(2025, 2, 5), false, 5, 5, true, List.of(p1));
        Review r3 = new Review("r3", "seller", dateOf(2025, 1, 15), dateOf(2025, 1, 15), false, 5, 5, true, List.of(p2));
        Review r4 = new Review("r4", "seller", dateOf(2025, 2, 10), dateOf(2025, 2, 10), false, 5, 5, false, List.of(p1));

        reviewService.insertReviews(List.of(r1, r2, r3, r4));

        // Act
        List<ProductMonthlyStatsDto> stats = reviewService.getMonthlyReviewsStats();

        // Assert
        assertThat(stats).hasSize(3);

        // Expect sorting by totalPositiveReviews desc, then productId, year, month
        // p1 has total 3, p2 has total 1
        ProductMonthlyStatsDto s1 = stats.get(0);
        ProductMonthlyStatsDto s2 = stats.get(1);
        ProductMonthlyStatsDto s3 = stats.get(2);

        // First two entries should be for product 1, months 1 and 2
        assertThat(s1.getProductId()).isEqualTo(1L);
        assertThat(s1.getTotalPositiveReviews()).isEqualTo(3L);
        assertThat(s1.getYear()).isEqualTo(2025);
        assertThat(s1.getMonth()).isEqualTo(1);
        assertThat(s1.getPositiveReviewsCount()).isEqualTo(1L);

        assertThat(s2.getProductId()).isEqualTo(1L);
        assertThat(s2.getTotalPositiveReviews()).isEqualTo(3L);
        assertThat(s2.getYear()).isEqualTo(2025);
        assertThat(s2.getMonth()).isEqualTo(2);
        assertThat(s2.getPositiveReviewsCount()).isEqualTo(2L);

        // Last entry is for product 2, month 1, total 1
        assertThat(s3.getProductId()).isEqualTo(2L);
        assertThat(s3.getTotalPositiveReviews()).isEqualTo(1L);
        assertThat(s3.getYear()).isEqualTo(2025);
        assertThat(s3.getMonth()).isEqualTo(1);
        assertThat(s3.getPositiveReviewsCount()).isEqualTo(1L);

        // Also verify titles and urls are propagated
        assertThat(s1.getProductTitle()).isEqualTo("Product 1");
        assertThat(s1.getProductUrl()).isEqualTo("http://example.com/p1");
        assertThat(s3.getProductTitle()).isEqualTo("Product 2");
        assertThat(s3.getProductUrl()).isEqualTo("http://example.com/p2");
    }

    private Date dateOf(int year, int month1Based, int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month1Based - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 12); // midday to avoid DST issues
        return cal.getTime();
    }
}
