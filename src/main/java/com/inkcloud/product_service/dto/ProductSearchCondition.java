package com.inkcloud.product_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductSearchCondition {

    private String keyword; // 검색어

    private List<String> searchFields; // 검색 대상 필드

    private List<Long> categoryIds; // 카테고리 필터링용
    
    private String sortType; // 정렬 방식

}
