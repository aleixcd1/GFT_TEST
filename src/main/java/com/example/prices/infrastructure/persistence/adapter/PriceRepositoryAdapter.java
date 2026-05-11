package com.example.prices.infrastructure.persistence.adapter;

import com.example.prices.domain.model.Price;
import com.example.prices.domain.port.out.PriceRepository;
import com.example.prices.infrastructure.persistence.entity.PriceEntity;
import com.example.prices.infrastructure.persistence.repository.JpaPriceRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PriceRepositoryAdapter implements PriceRepository {

    private final JpaPriceRepository jpaRepository;

    public PriceRepositoryAdapter(JpaPriceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Price> findByProductAndBrandAndDate(Long productId, Long brandId, LocalDateTime date) {

        List<PriceEntity> entities = jpaRepository
                .findApplicablePrices(productId, brandId, date);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private Price toDomain(PriceEntity e) {
        return new Price(
                e.getBrandId(),
                e.getStartDate(),
                e.getEndDate(),
                e.getPriceList(),
                e.getProductId(),
                e.getPriority(),
                e.getPrice(),
                e.getCurrency()
        );
    }
}