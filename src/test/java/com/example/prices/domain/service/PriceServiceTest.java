package com.example.prices.domain.service;

import com.example.prices.domain.exception.PriceNotFoundException;
import com.example.prices.domain.model.Price;
import com.example.prices.domain.port.out.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceService(priceRepository);
    }

    @Test
    void shouldReturnPriceWithHighestPriorityWhenMultiplePricesMatch() {
        // Given
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 16, 0);

        Price price1 = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        Price price2 = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .priceList(2)
                .priority(1)
                .price(new BigDecimal("25.45"))
                .currency("EUR")
                .build();

        when(priceRepository.findByProductAndBrandAndDate(productId, brandId, date))
                .thenReturn(List.of(price1, price2));

        // When
        Price result = priceService.getApplicablePrice(productId, brandId, date);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getPriceList());
        assertEquals(1, result.getPriority());
        assertEquals(new BigDecimal("25.45"), result.getPrice());
        verify(priceRepository, times(1)).findByProductAndBrandAndDate(productId, brandId, date);
    }

    @Test
    void shouldReturnSinglePriceWhenOnlyOneMatches() {
        // Given
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price price = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        when(priceRepository.findByProductAndBrandAndDate(productId, brandId, date))
                .thenReturn(List.of(price));

        // When
        Price result = priceService.getApplicablePrice(productId, brandId, date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPriceList());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenNoPriceFound() {
        // Given
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.of(2025, 1, 1, 10, 0);

        when(priceRepository.findByProductAndBrandAndDate(productId, brandId, date))
                .thenReturn(List.of());

        // When & Then
        PriceNotFoundException exception = assertThrows(
                PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(productId, brandId, date)
        );

        assertTrue(exception.getMessage().contains("No price found"));
    }

    @Test
    void shouldFilterOutNonApplicablePrices() {
        // Given
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price applicablePrice = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        Price nonApplicablePrice = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 15, 0, 0))
                .endDate(LocalDateTime.of(2020, 6, 15, 11, 0))
                .priceList(3)
                .priority(1)
                .price(new BigDecimal("30.50"))
                .currency("EUR")
                .build();

        when(priceRepository.findByProductAndBrandAndDate(productId, brandId, date))
                .thenReturn(List.of(applicablePrice, nonApplicablePrice));

        // When
        Price result = priceService.getApplicablePrice(productId, brandId, date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPriceList());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
    }
}
