package org.example.dto;

import java.util.Date;
import java.util.List;

public class ReviewDto {
    private String id;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean ratedAgain;

    private boolean recommend;

    private List<ProductDto> offers;

    private RatesDto rates;

    public ReviewDto(String id, Date creationDate, Date lastChangeDate, boolean ratedAgain, boolean recommend, List<ProductDto> offers, RatesDto rates) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastChangeDate = lastChangeDate;
        this.ratedAgain = ratedAgain;
        this.recommend = recommend;
        this.offers = offers;
        this.rates = rates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public RatesDto getRates() {
        return rates;
    }

    public void setRates(RatesDto rates) {
        this.rates = rates;
    }

    public List<ProductDto> getOffers() {
        return offers;
    }

    public void setOffers(List<ProductDto> offers) {
        this.offers = offers;
    }
}
