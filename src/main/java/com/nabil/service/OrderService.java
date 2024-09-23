package com.nabil.service;

import com.nabil.domain.OrderType;
import com.nabil.model.Coin;
import com.nabil.model.Order;
import com.nabil.model.OrderItem;
import com.nabil.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);
    Order getOrderById(Long orderId) throws Exception;
    List<Order> getUserAllOrders(Long userId, OrderType orderType, String assetSymbol);
    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
