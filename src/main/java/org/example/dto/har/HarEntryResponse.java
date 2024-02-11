package org.example.dto.har;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)


public class HarEntryResponse {
    private int status;
    private HarEntryResponseContent content;

    public HarEntryResponse(int status, HarEntryResponseContent content) {
        this.status = status;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HarEntryResponseContent getContent() {
        return content;
    }

    public void setContent(HarEntryResponseContent content) {
        this.content = content;
    }
}
