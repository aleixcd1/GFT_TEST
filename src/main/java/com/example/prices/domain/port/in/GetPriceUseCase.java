package com.example.prices.domain.port.in;

import com.example.prices.domain.model.Price;

import java.time.LocalDateTime;

public interface GetPriceUseCase {

    Price execute(Long productId,
                  Long brandId,
                  LocalDateTime applicationDate);
}
