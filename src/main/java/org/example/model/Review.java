package org.example.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @Column(name = "id", length = 24)
    private String id;

    @Column(name = "seller", nullable = false, length = 25)
    private String seller;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_change_date", nullable = false)
    private Date lastChangeDate;

    @Column(name = "rated_again", nullable = false)
    private boolean ratedAgain;

    @Column(name = "description_rating", nullable = false)
    private int descriptionRating;

    @Column(name = "service_rating", nullable = false)
    private int serviceRating;

    @Column(name = "recommend", nullable = false)
    private boolean recommend;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "reviews_products",
            joinColumns = @JoinColumn(name = "review_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    private List<Product> products;
}
