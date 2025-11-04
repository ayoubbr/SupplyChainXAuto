package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.ProductRequest;
import ma.youcode.supplyChainX.dto.ProductResponse;
import ma.youcode.supplyChainX.mapper.ProductMapper;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import ma.youcode.supplyChainX.model.Product;
import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.repository.ProductRepository;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;


    public ProductResponse save(ProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setCost(request.getCost());
        product.setStock(request.getStock());
        product.setProductionTime(request.getProductionTime());

        List<BillOfMaterial> materials = request.getBillOfMaterials().stream().map(dto -> {
            RawMaterial rm = rawMaterialRepository.findById(dto.getRawMaterialId()).orElseThrow(() -> new IllegalArgumentException("Raw material not found: " + dto.getRawMaterialId()));

            BillOfMaterial bom = new BillOfMaterial();
            bom.setRawMaterial(rm);
            bom.setProduct(product);
            bom.setQuantity(dto.getQuantity());
            return bom;
        }).collect(Collectors.toList());

        product.setBillOfMaterials(materials);
        validateProduct(product);
        Product saved = productRepository.save(product);
        return productMapper.mapToResponseDTO(saved);
    }

    public ProductResponse update(ProductRequest productRequest, Long id) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));


        existingProduct.setName(productRequest.getName());
        existingProduct.setCost(productRequest.getCost());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setProductionTime(productRequest.getProductionTime());

        if (productRequest.getBillOfMaterials() != null && !productRequest.getBillOfMaterials().isEmpty()) {
            existingProduct.getBillOfMaterials().clear();
            List<BillOfMaterial> updatedBoms = productRequest.getBillOfMaterials().stream().map(dto -> {
                RawMaterial rm = rawMaterialRepository.findById(dto.getRawMaterialId()).orElseThrow(() -> new EntityNotFoundException("Raw material not found: " + dto.getRawMaterialId()));
                BillOfMaterial bom = new BillOfMaterial();
                bom.setProduct(existingProduct);
                bom.setRawMaterial(rm);
                bom.setQuantity(dto.getQuantity());
                return bom;
            }).toList();
            existingProduct.getBillOfMaterials().addAll(updatedBoms);
        }

        validateProduct(existingProduct);

        Product updated = productRepository.save(existingProduct);

        return productMapper.mapToResponseDTO(updated);
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.getOrders().isEmpty() || !product.getProductionOrders().isEmpty()) {
            throw new IllegalStateException("Cannot delete product with existing associations");
        }

        productRepository.deleteById(id);
    }

    public List<ProductResponse> findAll() {
        List<Product> all = productRepository.findAll();
        return all.stream().map(productMapper::mapToResponseDTO).collect(Collectors.toList());
    }

    public ProductResponse findByName(String name) {
        Product product = productRepository.findByName(name);
        if (product == null) {
            throw new EntityNotFoundException("Product not found with name: " + name);
        }
        return productMapper.mapToResponseDTO(product);
    }

    private void validateProduct(Product product) {
        if (product.getCost() < 0) throw new IllegalArgumentException("Product cost cannot be negative");
        if (product.getStock() < 0) throw new IllegalArgumentException("Product stock cannot be negative");
        if (product.getProductionTime() < 0) throw new IllegalArgumentException("Production time cannot be negative");
    }
}
