package com.example.stock_price_server;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = StockController.class)
class StockControllerTests {
	@Mock
	private ThirdPartAppProperties thirdPartAppProperties;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private YahooFinanceAPI yahooFinanceAPI;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
	}

	@Test
	void stockControllerTestGET() throws Exception {
		var ticker = "TSLA";
		var value = 100.00;
		var response = objectMapper.writeValueAsString(new StockPriceResponse(ticker, value));

		// Arrange
		var thirdPartyAPIResponse = new APIStockChartResponse(
				new Chart(List.of(
						new Result(
								new Meta("USD", ticker, value, "Tesla")))));
		when(yahooFinanceAPI.fetchStockInfo(ticker)).thenReturn(thirdPartyAPIResponse);

		when(thirdPartAppProperties.getBaseUrl()).thenReturn("http://localhost-teste/api/v1/");

		// Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stock/" + ticker))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(response));
	}
}
