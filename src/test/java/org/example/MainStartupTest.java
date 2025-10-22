package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
class MainStartupTest {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test will fail.
    }
}
