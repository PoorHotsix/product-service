package com.inkcloud.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductQuantityChangeResult {

    String orderId;
    Boolean isSuccessful;
    
}
