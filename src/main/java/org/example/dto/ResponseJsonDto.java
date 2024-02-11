package org.example.dto;

import java.util.List;

public class ResponseJsonDto {
    private List<ReviewDto> content;

    public ResponseJsonDto(List<ReviewDto> content) {
        this.content = content;
    }

    public List<ReviewDto> getContent() {
        return content;
    }

    public void setContent(List<ReviewDto> content) {
        this.content = content;
    }
}
