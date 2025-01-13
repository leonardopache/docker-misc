package com.example.stock_price_server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class YahooFinanceAPITest {

	@Mock
	private ThirdPartAppProperties thirdPartAppProperties;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private YahooFinanceAPI yahooFinanceAPI;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}

	@Test
	void YahooFinanceAPITestGET() throws JsonMappingException, JsonProcessingException {
		// Arrange
		var baseUrl = "http://localhost-teste/api/v1/";
		var url = baseUrl + "finance/chart/";
		var apiJsonResponse = """
				{
					"chart": {
						"result": [
						{
							"meta": {
							"currency": "USD",
							"symbol": "TSLA",
							"exchangeName": "NMS",
							"fullExchangeName": "NasdaqGS",
							"instrumentType": "EQUITY",
							"firstTradeDate": 1277818200,
							"regularMarketTime": 1733157320,
							"hasPrePostMarketData": true,
							"gmtoffset": -18000,
							"timezone": "EST",
							"exchangeTimezoneName": "America/New_York",
							"regularMarketPrice": 352.73,
							"fiftyTwoWeekHigh": 361.93,
							"fiftyTwoWeekLow": 138.8,
							"regularMarketDayHigh": 358.78,
							"regularMarketDayLow": 351.15,
							"regularMarketVolume": 36964560,
							"longName": "Tesla, Inc.",
							"shortName": "Tesla, Inc.",
							"chartPreviousClose": 345.16,
							"previousClose": 345.16,
							"scale": 3,
							"priceHint": 2,
							"dataGranularity": "1m",
							"range": "1d"
							},
							"timestamp": [],
							"indicators": {}
						}
						],
						"error": null
					}
					}
				""";
		var body = objectMapper.readValue(apiJsonResponse, APIStockChartResponse.class);
		ResponseEntity<APIStockChartResponse> myEntity = new ResponseEntity<APIStockChartResponse>(body,
				HttpStatus.ACCEPTED);
		// Mock the RestTemplate call
		when(restTemplate.exchange(
				url + "TSLA",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<APIStockChartResponse>() {
				}))
				.thenReturn(myEntity);
			
		when(thirdPartAppProperties.getBaseUrl()).thenReturn(baseUrl);
		// Act
		APIStockChartResponse response = yahooFinanceAPI.fetchStockInfo("TSLA");

		// Assert
		assertEquals(body, response);

		// Verify interaction
		verify(restTemplate, times(1))
				.exchange(
						url + "TSLA",
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<APIStockChartResponse>() {});
	}
}
