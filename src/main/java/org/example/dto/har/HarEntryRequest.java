package org.example.dto.har;

public class HarEntryRequest {
    private String url;

    public HarEntryRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
