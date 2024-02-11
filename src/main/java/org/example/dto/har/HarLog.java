package org.example.dto.har;

import java.util.List;

public class HarLog {
    private List<HarEntry> entries;

    public HarLog(List<HarEntry> entries) {
        this.entries = entries;
    }

    public void setEntries(List<HarEntry> entries) {
        this.entries = entries;
    }

    public List<HarEntry> getEntries() {
        return entries;
    }
}
