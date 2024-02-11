package org.example.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReviewDto {
    private UUID id;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean ratedAgain;

    private boolean recommend;

    private List<ProductDto> offers;

    private RatesDto ratesDto;

    public ReviewDto(UUID id, Date creationDate, Date lastChangeDate, boolean ratedAgain, boolean recommend, List<ProductDto> offers, RatesDto ratesDto) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastChangeDate = lastChangeDate;
        this.ratedAgain = ratedAgain;
        this.recommend = recommend;
        this.offers = offers;
        this.ratesDto = ratesDto;
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

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public RatesDto getRatesDto() {
        return ratesDto;
    }

    public void setRatesDto(RatesDto ratesDto) {
        this.ratesDto = ratesDto;
    }

    public List<ProductDto> getOffers() {
        return offers;
    }

    public void setOffers(List<ProductDto> offers) {
        this.offers = offers;
    }
}
