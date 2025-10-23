package org.example.repository;

import org.example.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    @Query("select distinct r from Review r join fetch r.products p")
    List<Review> findAllWithProducts();
}
