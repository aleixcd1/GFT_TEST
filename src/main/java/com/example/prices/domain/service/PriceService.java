package com.example.prices.domain.service;

import com.example.prices.domain.model.Price;
import com.example.prices.domain.port.out.PriceRepository;
import com.example.prices.domain.exception.PriceNotFoundException;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Comparator;

public class PriceService {
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        List<Price> prices = priceRepository.findByProductAndBrandAndDate(productId, brandId, applicationDate);

        List<Price> validPrices = prices.stream()
                .filter(p -> p.isApplicable(applicationDate))
                .toList();

        if (validPrices.isEmpty()) {
            throw new PriceNotFoundException(
                    "No price found for product " + productId +
                            ", brand " + brandId +
                            ", date " + applicationDate
            );
        }

        return validPrices.stream()
                .max(Comparator.comparing(Price::getPriority))
                .orElseThrow();
    }
}
