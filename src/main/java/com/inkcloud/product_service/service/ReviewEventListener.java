package com.inkcloud.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.dto.ReviewEventDto;
import com.inkcloud.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;  // Spring에서 자동으로 주입됨

    @KafkaListener(topics = "review-rating-update", groupId = "product-service")
    @Transactional
    public void handleReviewEvent(String eventJson) {
        try {
            log.info("리뷰 이벤트 JSON 수신: {}", eventJson);

            // JSON 문자열을 ReviewEventDto로 변환
            ReviewEventDto event = objectMapper.readValue(eventJson, ReviewEventDto.class);
            log.info("변환된 리뷰 이벤트: {}", event);

            // 상품 조회
            Product product = productRepository.findById(event.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 기존 평점과 리뷰 개수
            double oldTotal = product.getRating() * product.getReviewsCount();
            int reviewsCount = product.getReviewsCount();
            double newAverage;

            if ("created".equals(event.getType())) {
                // 리뷰 작성: 리뷰 수 증가, 새 평점 추가
                reviewsCount += 1;
                newAverage = (oldTotal + event.getRating()) / reviewsCount;
                product.setReviewsCount(reviewsCount);
                log.info("리뷰 작성: 평점 {}, 리뷰 수 {}", newAverage, reviewsCount);

            } else if ("updated".equals(event.getType())) {
                // 리뷰 수정: oldRating 필요, 리뷰 수는 그대로
                if (event.getOldRating() == null) {
                    throw new IllegalArgumentException("리뷰 수정 이벤트에는 oldRating이 필요합니다.");
                }
                newAverage = (oldTotal - event.getOldRating() + event.getRating()) / reviewsCount;
                log.info("리뷰 수정: 평점 {}, 리뷰 수 {}", newAverage, reviewsCount);

            } else if ("deleted".equals(event.getType())) {
                // 리뷰 삭제: oldRating 필요, 리뷰 수 감소
                if (event.getOldRating() == null) {
                    throw new IllegalArgumentException("리뷰 삭제 이벤트에는 oldRating이 필요합니다.");
                }
                if (reviewsCount <= 1) {
                    // 마지막 리뷰 삭제 시 0으로 처리
                    newAverage = 0.0;
                    reviewsCount = 0;
                } else {
                    reviewsCount -= 1;
                    newAverage = (oldTotal - event.getOldRating()) / reviewsCount;
                }
                product.setReviewsCount(reviewsCount);
                log.info("리뷰 삭제: 평점 {}, 리뷰 수 {}", newAverage, reviewsCount);

            } else {
                log.warn("알 수 없는 이벤트 타입: {}", event.getType());
                return;
            }

            product.setRating(newAverage);
            productRepository.save(product);

            log.info("상품 {}의 평균 평점 갱신: {} (리뷰 수: {})", event.getProductId(), newAverage, reviewsCount);

        } catch (Exception e) {
            log.error("카프카 메시지 처리 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
