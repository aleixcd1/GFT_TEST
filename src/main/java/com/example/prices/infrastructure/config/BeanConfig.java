package com.example.prices.infrastructure.config;

import com.example.prices.application.GetPriceUseCaseImpl;
import com.example.prices.domain.port.in.GetPriceUseCase;
import com.example.prices.domain.port.out.PriceRepository;
import com.example.prices.domain.service.PriceService;
import com.example.prices.infrastructure.persistence.adapter.PriceRepositoryAdapter;
import com.example.prices.infrastructure.persistence.repository.JpaPriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PriceRepository priceRepository(JpaPriceRepository jpaRepository) {
        return new PriceRepositoryAdapter(jpaRepository);
    }

    @Bean
    public PriceService priceService(PriceRepository priceRepository) {
        return new PriceService(priceRepository);
    }

    @Bean
    public GetPriceUseCase getPriceUseCase(PriceService priceService) {
        return new GetPriceUseCaseImpl(priceService);
    }
}