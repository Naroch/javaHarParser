package org.example.dto;

import java.util.List;

public class EntriesJsonDto {
    private List<ReviewDto> content;

    public EntriesJsonDto(List<ReviewDto> content) {
        this.content = content;
    }

    public List<ReviewDto> getContent() {
        return content;
    }

    public void setContent(List<ReviewDto> content) {
        this.content = content;
    }
}
