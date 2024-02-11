package org.example.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Review {
    private UUID id;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean ratedAgain;

    private int descriptionRating;
    private int serviceRating;
    private boolean recommend;

    private List<Product> products;

    public Review(UUID id, Date creationDate, Date lastChangeDate, boolean ratedAgain, int descriptionRating, int serviceRating, boolean recommend, List<Product> products) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastChangeDate = lastChangeDate;
        this.ratedAgain = ratedAgain;
        this.descriptionRating = descriptionRating;
        this.serviceRating = serviceRating;
        this.recommend = recommend;
        this.products = products;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(Date lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public boolean isRatedAgain() {
        return ratedAgain;
    }

    public void setRatedAgain(boolean ratedAgain) {
        this.ratedAgain = ratedAgain;
    }

    public int getDescriptionRating() {
        return descriptionRating;
    }

    public void setDescriptionRating(int descriptionRating) {
        this.descriptionRating = descriptionRating;
    }

    public int getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(int serviceRating) {
        this.serviceRating = serviceRating;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
