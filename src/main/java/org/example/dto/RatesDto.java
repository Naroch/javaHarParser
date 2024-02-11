package org.example.dto;

public class RatesDto {
    private int description;
    private int service;

    public RatesDto(int description, int service) {
        this.description = description;
        this.service = service;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
