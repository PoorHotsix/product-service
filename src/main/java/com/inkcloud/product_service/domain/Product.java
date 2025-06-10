package com.inkcloud.product_service.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;

    private String name;

    private String author;

    private String publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private int price;

    private LocalDate publicationDate;

    @Column(length = 1000)
    private String introduction;

    private String image; // S3 URL

    private int quantity;

    private double rating; // 평균 평점

    private int reviewsCount; // 리뷰 개수

    private int ordersCount; // 주문량

    @Enumerated(EnumType.STRING)
    private Status status; // 상품 상태 정보

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 소수 첫째자리로 반올림
    // public void setRating(double rating) {
    //     this.rating = Math.round(rating * 10) / 10.0;
    // }

    // public double getRating() {
    //     return rating;
    // }

}
