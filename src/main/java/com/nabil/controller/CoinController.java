package com.nabil.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabil.model.Coin;
import com.nabil.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    private ObjectMapper objectMapper;

    @GetMapping
    ResponseEntity<List<Coin>> getCoinList(@RequestParam(name="page") int page) throws Exception {
        return new ResponseEntity<>(coinService.getCoinList(page), HttpStatus.OK);
    }

    @GetMapping("{coinId}/chart")
    ResponseEntity<JsonNode> getMarketChart(@RequestParam(name="days") int days, @PathVariable String coinId) throws Exception {
        String response = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/search")
    ResponseEntity<JsonNode> searchCoin(@RequestParam(name="query") String keyword) throws Exception {
        String response = coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/top50")
    ResponseEntity<JsonNode> getTop50CoinsByMarketCapRank() throws Exception {
        String response = coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/treading")
    ResponseEntity<JsonNode> getTreadingCoins() throws Exception {
        String response = coinService.getTreadingCoins();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/details/{coinId}")
    ResponseEntity<JsonNode> getTreadingCoins(@PathVariable String coinId) throws Exception {
        String response = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/{coinId}")
    ResponseEntity<Coin> findCoinById(@PathVariable String coinId) throws Exception {
        Coin coin = coinService.findById(coinId);
        return new ResponseEntity<>(coin, HttpStatus.OK);
    }
}
