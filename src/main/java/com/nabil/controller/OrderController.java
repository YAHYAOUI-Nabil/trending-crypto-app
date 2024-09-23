package com.nabil.controller;

import com.nabil.domain.OrderType;
import com.nabil.model.Coin;
import com.nabil.model.Order;
import com.nabil.model.User;
import com.nabil.request.CreateOrderRequest;
import com.nabil.service.CoinService;
import com.nabil.service.OrderService;
import com.nabil.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CoinService coinService;
    private final WalletTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserProfileByJwt(token);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization") String token,
                                              @PathVariable Long orderId) throws Exception {
        if(token == null) {
            throw new Exception("token is missing...");
        }

        User user = userService.findUserProfileByJwt(token);
        Order order = orderService.getOrderById(orderId);

        if(order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserAllOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderType orderType,
            @RequestParam(required = false) String assetSymbol) throws Exception {

        if(token == null) {
            throw new Exception("token is missing...");
        }

        User user = userService.findUserProfileByJwt(token);
        List<Order> orders = orderService.getUserAllOrders(user.getId(), orderType, assetSymbol);

        return ResponseEntity.ok(orders);

    }


}
