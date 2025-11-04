package ma.youcode.supplyChainX.controller;

import ma.youcode.supplyChainX.dto.ProductRequest;
import ma.youcode.supplyChainX.dto.ProductResponse;
import ma.youcode.supplyChainX.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse addProduct(@RequestBody ProductRequest productRequest) {
        return productService.save(productRequest);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{name}")
    public ProductResponse getProductByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return productService.update(productRequest, id);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

}
