package org.example.dto;

import java.util.UUID;

public class ProductDto {
    private int id;
    private UUID orderOfferId;
    private String title;
    private String url;

    public ProductDto(int id, UUID orderOfferId, String title, String url) {
        this.id = id;
        this.orderOfferId = orderOfferId;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getOrderOfferId() {
        return orderOfferId;
    }

    public void setOrderOfferId(UUID orderOfferId) {
        this.orderOfferId = orderOfferId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
