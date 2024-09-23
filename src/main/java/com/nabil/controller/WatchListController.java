package com.nabil.controller;

import com.nabil.model.Coin;
import com.nabil.model.User;
import com.nabil.model.WatchList;
import com.nabil.service.CoinService;
import com.nabil.service.UserService;
import com.nabil.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {

    private final WatchListService watchListService;
    private final UserService userService;
    private final CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchList(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        WatchList watchList = watchListService.findUserWatchList(user.getId());

        return new ResponseEntity<>(watchList, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<WatchList> createUserWatchList(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        WatchList watchList = watchListService.createUserWatchList(user);

        return new ResponseEntity<>(watchList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WatchList> getWatchListById(@PathVariable Long id) throws Exception {

        WatchList watchList = watchListService.findById(id);

        return new ResponseEntity<>(watchList, HttpStatus.OK);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchList(
            @RequestHeader("Authorization") String token,
            @PathVariable String coinId) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        Coin coin = coinService.findById(coinId);

        watchListService.addItemToWatchList(coin, user);

        return new ResponseEntity<>(coin, HttpStatus.OK);
    }
}
