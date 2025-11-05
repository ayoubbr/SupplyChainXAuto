package ma.youcode.supplyChainX.controller;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.DeliveryRequest;
import ma.youcode.supplyChainX.dto.DeliveryResponse;
import ma.youcode.supplyChainX.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public DeliveryResponse createDelivery(@RequestBody DeliveryRequest request) {
        return deliveryService.createDelivery(request);
    }
}

