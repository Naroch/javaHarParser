package org.example.dto.har;

public class HarEntryResponseContent {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HarEntryResponseContent(String text) {
        this.text = text;
    }
}
