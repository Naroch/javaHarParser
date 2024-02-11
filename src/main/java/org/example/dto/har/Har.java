package org.example.dto.har;

public class Har {
    private HarLog log;

    public HarLog getLog() {
        return log;
    }

    public void setLog(HarLog log) {
        this.log = log;
    }

    public Har(HarLog log) {
        this.log = log;
    }
}
