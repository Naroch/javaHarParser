package org.example.integration;

import org.example.HarProcessingService;
import org.example.Main;
import org.example.integration.setup.DatabaseCleanup;
import org.example.integration.setup.TestcontainersConfiguration;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class HarImportIntegrationTest {

    @Autowired
    private HarProcessingService harProcessingService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.cleanDatabase();
    }

    @Test
    void should_import_reviews_from_har_and_persist_in_database() throws IOException {
        // Arrange: copy test HAR resource to a temp file with seller prefix "importokazji"
        File tempHar = copyResourceHarToTempWithExactName("/harfiles/importokazji allegro.pl.har", "importokazji allegro.pl.har");
        try {
            // Act
            harProcessingService.processHarFile(tempHar);

            // Assert
            long count = reviewRepository.count();
            assertThat(count).as("Reviews should be imported from HAR into DB").isGreaterThan(0);

            List<Review> all = reviewRepository.findAll();
            assertThat(all).allMatch(r -> "importokazji".equals(r.getSeller()));
        } finally {
            // Cleanup the temp file
            Files.deleteIfExists(tempHar.toPath());
        }
    }

    private File copyResourceHarToTempWithExactName(String resourcePath, String fileName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Test resource not found: " + resourcePath);
            }
            File tempDir = Files.createTempDirectory("har-test-").toFile();
            File target = new File(tempDir, fileName);
            try (FileOutputStream os = new FileOutputStream(target)) {
                is.transferTo(os);
            }
            return target;
        }
    }
}
