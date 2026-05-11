package com.example.prices.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1_requestAt10AMOn14th_shouldReturnPrice35_50() throws Exception {
        // Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)
        String requestBody = """
                {
                    "productId": 35455,
                    "brandId": 1,
                    "applicationDate": "2020-06-14T10:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test2_requestAt4PMOn14th_shouldReturnPrice25_45() throws Exception {
        // Test 2: petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA)
        String requestBody = """
                {
                    "productId": 35455,
                    "brandId": 1,
                    "applicationDate": "2020-06-14T16:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00"))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test3_requestAt9PMOn14th_shouldReturnPrice35_50() throws Exception {
        // Test 3: petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA)
        String requestBody = """
                {
                    "productId": 35455,
                    "brandId": 1,
                    "applicationDate": "2020-06-14T21:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test4_requestAt10AMOn15th_shouldReturnPrice30_50() throws Exception {
        // Test 4: petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA)
        String requestBody = """
                {
                    "productId": 35455,
                    "brandId": 1,
                    "applicationDate": "2020-06-15T10:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-15T11:00:00"))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void test5_requestAt9PMOn16th_shouldReturnPrice38_95() throws Exception {
        // Test 5: petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA)
        String requestBody = """
                {
                    "productId": 35455,
                    "brandId": 1,
                    "applicationDate": "2020-06-16T21:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.startDate").value("2020-06-15T16:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.price").value(38.95))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void testValidation_shouldReturn400WhenProductIdIsNull() throws Exception {
        String requestBody = """
                {
                    "brandId": 1,
                    "applicationDate": "2020-06-14T10:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testNotFound_shouldReturn404WhenNoPriceExists() throws Exception {
        String requestBody = """
                {
                    "productId": 99999,
                    "brandId": 1,
                    "applicationDate": "2020-06-14T10:00:00"
                }
                """;

        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("No price found for product 99999, brand 1, date 2020-06-14T10:00"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
