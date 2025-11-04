package ma.youcode.supplyChainX.controller;

import ma.youcode.supplyChainX.dto.ProductionOrderRequest;
import ma.youcode.supplyChainX.dto.ProductionOrderResponse;
import ma.youcode.supplyChainX.model.ProductionOrder;
import ma.youcode.supplyChainX.service.ProductionOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production-orders")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    public ProductionOrderController(ProductionOrderService productionOrderService) {
        this.productionOrderService = productionOrderService;
    }

    @PostMapping
    public ProductionOrderResponse create(@RequestBody ProductionOrderRequest request) {
        return productionOrderService.save(request);
    }

    @GetMapping
    public List<ProductionOrderResponse> getAll() {
        return productionOrderService.getAll();
    }

    @GetMapping("/{id}")
    public ProductionOrderResponse getById(@PathVariable Long id) {
        return productionOrderService.getById(id);
    }

    @PutMapping("/{id}")
    public ProductionOrderResponse update(@RequestBody ProductionOrderRequest productionOrderRequest, Long id) {
        return productionOrderService.update(productionOrderRequest, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productionOrderService.delete(id);
    }

    @PutMapping("/cancel/{id}")
    public void cancel(@PathVariable Long id) {
        productionOrderService.cancel(id);
    }


}
