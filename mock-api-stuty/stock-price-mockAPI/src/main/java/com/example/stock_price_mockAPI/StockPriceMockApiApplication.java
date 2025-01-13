package com.example.stock_price_mockAPI;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@SpringBootApplication
public class StockPriceMockApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockPriceMockApiApplication.class, args);
	}

}

@RestController
@RequestMapping("/")
@AllArgsConstructor
class JsonController {
    private ResourceLoader resourceLoader;

    @GetMapping("finance/chart/{ticker}")
    public ResponseEntity<JsonNode> getJsonData(@PathVariable String ticker) {
        try {
            // Load the JSON file from resources
            var resource = resourceLoader.getResource("classpath:static/" + ticker.toUpperCase() + ".json");
	
            // Parse the JSON file into a JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());

            // Return the JSON as the response
            return ResponseEntity.ok(jsonNode);

        } catch (IOException e) {
            // Handle the error appropriately
            return ResponseEntity.internalServerError().build();
        }
    }
}