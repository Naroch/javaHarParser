package org.example.model;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private String id;
    private String seller;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean ratedAgain;

    private int descriptionRating;
    private int serviceRating;
    private boolean recommend;

    private List<Product> products;
}
