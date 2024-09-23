package com.nabil.repository;

import com.nabil.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByUser(Long userId);

    Asset findByUserIdAndCoinId(Long userId, String CoinId);
}
