package ma.youcode.supplyChainX.controller;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.OrderRequest;
import ma.youcode.supplyChainX.dto.OrderResponse;
import ma.youcode.supplyChainX.model.Order;
import ma.youcode.supplyChainX.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.save(orderRequest);
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        return orderService.update(id, orderRequest);
    }

    @PutMapping("/cancel/{id}")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancel(id);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getById(id);
    }

}
