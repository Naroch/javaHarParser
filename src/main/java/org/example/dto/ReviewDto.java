package org.example.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private String id;
    private Date creationDate;
    private Date lastChangeDate;
    private boolean ratedAgain;
    private boolean recommend;
    private List<ProductDto> offers;
    private CommentDto comment;
}
