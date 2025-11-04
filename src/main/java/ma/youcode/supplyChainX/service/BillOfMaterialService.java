package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.BillOfMaterialRequest;
import ma.youcode.supplyChainX.dto.BillOfMaterialResponse;
import ma.youcode.supplyChainX.mapper.BillOfMaterialMapper;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import ma.youcode.supplyChainX.model.Product;
import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.repository.BillOfMaterialRepository;
import ma.youcode.supplyChainX.repository.ProductRepository;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BillOfMaterialService {

    private final BillOfMaterialRepository billOfMaterialRepository;
    private final BillOfMaterialMapper billOfMaterialMapper;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public BillOfMaterialResponse save(BillOfMaterialRequest billOfMaterialRequest) {
        validateRequest(billOfMaterialRequest);

        // Check duplicate
        if (billOfMaterialRepository.findByProductIdAndRawMaterialId(billOfMaterialRequest.getProductId(),
                billOfMaterialRequest.getRawMaterialId()) != null) {
            throw new IllegalArgumentException("A BillOfMaterial with the same Product and Raw Material already exists");
        }
        Product product = productRepository.findById(billOfMaterialRequest.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + billOfMaterialRequest.getProductId()));

        RawMaterial rawMaterial = rawMaterialRepository.findById(billOfMaterialRequest.getRawMaterialId())
                .orElseThrow(() -> new EntityNotFoundException("Raw material not found: " + billOfMaterialRequest.getRawMaterialId()));


        BillOfMaterial bom = billOfMaterialMapper.toEntity(billOfMaterialRequest, rawMaterial, product);
        BillOfMaterial saved = billOfMaterialRepository.save(bom);

        return billOfMaterialMapper.mapBillOfMaterial(saved);
    }

    public BillOfMaterialResponse update(BillOfMaterialRequest request, Long id) {
        requireValidId(id);
        validateRequest(request);

        BillOfMaterial existing = billOfMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BillOfMaterial not found with ID: " + id));

        BillOfMaterial duplicate = billOfMaterialRepository
                .findByProductIdAndRawMaterialId(request.getProductId(), request.getRawMaterialId());

        // If a duplicate exists, and it’s not the same record, reject
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new IllegalArgumentException("A BillOfMaterial with the same Product and Raw Material already exists");
        }

        // Update fields
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.getProductId()));

        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new EntityNotFoundException("Raw material not found: " + request.getRawMaterialId()));

        existing.setProduct(product);
        existing.setRawMaterial(rawMaterial);
        existing.setQuantity(request.getQuantityPerProduct());

        BillOfMaterial updated = billOfMaterialRepository.save(existing);
        return billOfMaterialMapper.mapBillOfMaterial(updated);
    }

    public void delete(Long id) {
        requireValidId(id);

        BillOfMaterial bom = billOfMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BillOfMaterial not found: " + id));

        billOfMaterialRepository.delete(bom);
    }

    public BillOfMaterialResponse getById(Long id) {
        requireValidId(id);

        BillOfMaterial bom = billOfMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BillOfMaterial not found: " + id));

        return billOfMaterialMapper.mapBillOfMaterial(bom);
    }

    public List<BillOfMaterialResponse> getAll() {
        return billOfMaterialRepository.findAll().stream()
                .map(billOfMaterialMapper::mapBillOfMaterial)
                .collect(Collectors.toList());
    }

    private void validateRequest(BillOfMaterialRequest request) {
        if (request == null) throw new IllegalArgumentException("Request cannot be null");
        if (request.getProductId() == null) throw new IllegalArgumentException("Product ID cannot be null");
        if (request.getRawMaterialId() == null) throw new IllegalArgumentException("Raw Material ID cannot be null");
        if (request.getQuantityPerProduct() <= 0)
            throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    private void requireValidId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Invalid ID");
    }

}
