package org.example.dto.har;

public class HarEntry {
    private HarEntryRequest request;
    private HarEntryResponse response;

    public HarEntry(HarEntryRequest request, HarEntryResponse response) {
        this.request = request;
        this.response = response;
    }

    public void setRequest(HarEntryRequest request) {
        this.request = request;
    }

    public void setResponse(HarEntryResponse response) {
        this.response = response;
    }

    public HarEntryResponse getResponse() {
        return response;
    }

    public HarEntryRequest getRequest() {
        return request;
    }
}
