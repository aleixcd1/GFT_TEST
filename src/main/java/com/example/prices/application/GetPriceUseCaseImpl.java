package com.example.prices.application;

import com.example.prices.domain.model.Price;
import com.example.prices.domain.service.PriceService;
import com.example.prices.domain.port.in.GetPriceUseCase;

import java.time.LocalDateTime;

public class GetPriceUseCaseImpl implements GetPriceUseCase {

    private final PriceService priceService;

    public GetPriceUseCaseImpl(PriceService priceService) {
        this.priceService = priceService;
    }

    @Override
    public Price execute(Long productId, Long brandId, LocalDateTime applicationDate) {
        return priceService.getApplicablePrice(productId, brandId, applicationDate);
    }

}
