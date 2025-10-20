package org.example.dto.har;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HarEntryResponse {
    private int status;
    private HarEntryResponseContent content;
}
