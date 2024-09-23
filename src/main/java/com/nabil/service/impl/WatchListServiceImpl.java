package com.nabil.service.impl;

import com.nabil.model.Coin;
import com.nabil.model.User;
import com.nabil.model.WatchList;
import com.nabil.repository.WatchListRepository;
import com.nabil.service.UserService;
import com.nabil.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {

    private final WatchListRepository watchListRepository;
    private final UserService userService;
    @Override
    public WatchList findUserWatchList(Long userId) throws Exception {
        WatchList watchList = watchListRepository.findByUser(userId);

        if(watchList == null) {
            throw new Exception("watchList not found.");
        }
        return watchList;
    }

    @Override
    public WatchList createUserWatchList(User user) {
        WatchList watchList = new WatchList();
        watchList.setUser(user);

        return watchListRepository.save(watchList);
    }

    @Override
    public WatchList findById(Long id) throws Exception {
        Optional<WatchList> watchList = watchListRepository.findById(id);

        if(watchList.isEmpty()) {
            throw new Exception("watchList not found.");
        }
        return watchList.get();
    }

    @Override
    public Coin addItemToWatchList(Coin coin, User user) throws Exception {
        WatchList watchList = findUserWatchList(user.getId());

        if(watchList.getCoins().contains(coin)) {
            watchList.getCoins().remove(coin);
        } else {
            watchList.getCoins().add(coin);
        }
        watchListRepository.save(watchList);

        return null;
    }
}
