package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hars")
public class HarProcessingController {

    private final HarProcessingService harProcessingService;

    public HarProcessingController(HarProcessingService harProcessingService) {
        this.harProcessingService = harProcessingService;
    }

    @GetMapping("/process")
    public ResponseEntity<String> processHarFilesGet() {
        harProcessingService.processAllHarFiles();
        return ResponseEntity.ok("HAR processing started and completed for files in 'harfiles' folder.");
    }

    @PostMapping("/process")
    public ResponseEntity<String> processHarFilesPost() {
        harProcessingService.processAllHarFiles();
        return ResponseEntity.ok("HAR processing started and completed for files in 'harfiles' folder.");
    }
}
