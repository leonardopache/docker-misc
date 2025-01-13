package com.example.stock_price_server;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class StockPriceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockPriceServerApplication.class, args);
    }
}

// INITIALIZE
@Slf4j
@Component
@AllArgsConstructor
class SampleInitializer {

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
    }
}

@Configuration
class ThirdPartAppProperties {
    @Getter
    @Value("${third.part.api.base.url}")
    private String baseUrl;
}

@RestController
@RequestMapping("/api/v1/stock")
@AllArgsConstructor
class StockController {

    private final YahooFinanceAPI api;

    @GetMapping(path = "/{ticker}")
    public StockPriceResponse getStock(@PathVariable String ticker) {
        return Optional.ofNullable(api.fetchStockInfo(ticker))
                .map(APIStockChartResponse::getChart)
                .map(Chart::getResults)
                .map(list -> list.get(0))
                .map(Result::getMeta)
                .map(meta -> new StockPriceResponse(meta.getSymbol(), meta.getRegularMarketPrice()))
                .orElseThrow();
    }
}

record StockPriceResponse(String ticker, double value) {
}

@Configuration
class RestTemplateConfig {
    @Bean
    RestTemplate restTemplateClientDefault() {
        return new RestTemplate();
    }
}

@Service
@AllArgsConstructor
@Slf4j
class YahooFinanceAPI {
    private final RestTemplate restTemplate;
    private final ThirdPartAppProperties thirdPartAppProperties;

    private static final String API_URL = "finance/chart/";

    public APIStockChartResponse fetchStockInfo(String ticker) {
        var r = restTemplate.exchange(
                thirdPartAppProperties.getBaseUrl() + API_URL + ticker,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<APIStockChartResponse>() {
                });
        return r.getBody();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class APIStockChartResponse {
    private Chart chart;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Chart {
    @JsonProperty("result")
    private List<Result> results;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Result {
    private Meta meta;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Meta {
    private String currency;
    private String symbol;
    private double regularMarketPrice;
    private String longName;
}
