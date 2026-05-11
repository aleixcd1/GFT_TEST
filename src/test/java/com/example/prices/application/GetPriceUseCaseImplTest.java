package com.example.prices.application;

import com.example.prices.domain.model.Price;
import com.example.prices.domain.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPriceUseCaseImplTest {

    @Mock
    private PriceService priceService;

    private GetPriceUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetPriceUseCaseImpl(priceService);
    }

    @Test
    void shouldExecuteAndReturnPrice() {
        // Given
        Long productId = 35455L;
        Long brandId = 1L;
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price expectedPrice = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        when(priceService.getApplicablePrice(productId, brandId, date))
                .thenReturn(expectedPrice);

        // When
        Price result = useCase.execute(productId, brandId, date);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(brandId, result.getBrandId());
        assertEquals(1, result.getPriceList());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
        verify(priceService, times(1)).getApplicablePrice(productId, brandId, date);
    }

    @Test
    void shouldDelegateToServiceCorrectly() {
        // Given
        Long productId = 12345L;
        Long brandId = 2L;
        LocalDateTime date = LocalDateTime.of(2020, 7, 1, 12, 0);

        Price price = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(date)
                .endDate(date.plusDays(1))
                .priceList(5)
                .priority(2)
                .price(new BigDecimal("99.99"))
                .currency("USD")
                .build();

        when(priceService.getApplicablePrice(productId, brandId, date))
                .thenReturn(price);

        // When
        Price result = useCase.execute(productId, brandId, date);

        // Then
        assertEquals(price, result);
        verify(priceService).getApplicablePrice(productId, brandId, date);
    }
}
