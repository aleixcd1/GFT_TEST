package com.example.prices.infrastructure.controller;

import com.example.prices.api.PricesApi;
import com.example.prices.api.model.PriceRequest;
import com.example.prices.api.model.PriceResponse;
import com.example.prices.domain.model.Price;
import com.example.prices.domain.port.in.GetPriceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceController implements PricesApi {

    private final GetPriceUseCase useCase;

    public PriceController(GetPriceUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<PriceResponse> getPrice(PriceRequest priceRequest) {
        Price price = useCase.execute(
                priceRequest.getProductId(),
                priceRequest.getBrandId(),
                priceRequest.getApplicationDate()
        );

        PriceResponse response = new PriceResponse()
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .priceList(price.getPriceList())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .price(price.getPrice().doubleValue())
                .currency(price.getCurrency());

        return ResponseEntity.ok(response);
    }
}
