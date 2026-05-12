package com.example.prices.domain.port.out;

import com.example.prices.domain.model.Price;

import java.util.List;
import java.time.LocalDateTime;

public interface PriceRepository {
    List<Price> findByProductAndBrandAndDate(
            Long productId,
            Long brandId,
            LocalDateTime applicationDate
    );
}
