package ma.youcode.supplyChainX.service;

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

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BillOfMaterialService {

    private final BillOfMaterialRepository billOfMaterialRepository;
    private final BillOfMaterialMapper billOfMaterialMapper;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public BillOfMaterialResponse save(BillOfMaterialRequest billOfMaterialRequest) {
        if (billOfMaterialRequest == null) {
            throw new IllegalArgumentException("BillOfMaterial cannot be null");
        }
        if (billOfMaterialRequest.getProductId() == null) {
            throw new IllegalArgumentException("BillOfMaterial must be associated with a Product");
        }
        if (billOfMaterialRequest.getRawMaterialId() == null) {
            throw new IllegalArgumentException("BillOfMaterial must be associated with a Raw Material");
        }
        if (billOfMaterialRequest.getQuantityPerProduct() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        BillOfMaterial byProductIdAndRawMaterialId = billOfMaterialRepository
                .findByProductIdAndRawMaterialId(billOfMaterialRequest.getProductId(), billOfMaterialRequest.getRawMaterialId());

        if (byProductIdAndRawMaterialId == null) {

            Product product = productRepository.findById(billOfMaterialRequest.getProductId()).orElse(null);
            RawMaterial rawMaterial = rawMaterialRepository.findById(billOfMaterialRequest.getRawMaterialId()).orElse(null);


            BillOfMaterial billOfMaterial = billOfMaterialMapper.toEntity(billOfMaterialRequest, rawMaterial, product);
            BillOfMaterial saved = billOfMaterialRepository.save(billOfMaterial);

            return billOfMaterialMapper.mapBillOfMaterial(saved);
        } else {
            throw new IllegalArgumentException("A BillOfMaterial with the same Product and Raw Material already exists");
        }
    }

    public BillOfMaterialResponse update(BillOfMaterialRequest billOfMaterialRequest, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        BillOfMaterial existingBillOfMaterial = billOfMaterialRepository.findById(id).orElse(null);
        if (existingBillOfMaterial == null) {
            throw new IllegalArgumentException("BillOfMaterial with the given ID does not exist");
        }

        BillOfMaterial byProductIdAndRawMaterialId = billOfMaterialRepository
                .findByProductIdAndRawMaterialId(billOfMaterialRequest.getProductId(), billOfMaterialRequest.getRawMaterialId());

        if (byProductIdAndRawMaterialId == null) {

            if (productRepository.findById(billOfMaterialRequest.getProductId()).isPresent()) {
                existingBillOfMaterial.setProduct(productRepository.findById(billOfMaterialRequest.getProductId()).get());
            }

            if (rawMaterialRepository.findById(billOfMaterialRequest.getRawMaterialId()).isPresent()) {
                existingBillOfMaterial.setRawMaterial(rawMaterialRepository
                        .findById(billOfMaterialRequest.getRawMaterialId()).get());
            }

            existingBillOfMaterial.setQuantity(billOfMaterialRequest.getQuantityPerProduct());
            BillOfMaterial updated = billOfMaterialRepository.save(existingBillOfMaterial);
            return billOfMaterialMapper.mapBillOfMaterial(updated);

        } else {
            throw new IllegalArgumentException("A BillOfMaterial with the same Product and Raw Material already exists");
        }
    }

    public BillOfMaterialResponse delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        BillOfMaterial existingBillOfMaterial = billOfMaterialRepository.findById(id).orElse(null);
        if (existingBillOfMaterial != null) {
            billOfMaterialRepository.deleteById(id);
            return billOfMaterialMapper.mapBillOfMaterial(existingBillOfMaterial);
        }
        return null;
    }

    public BillOfMaterialResponse getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        BillOfMaterial billOfMaterial = billOfMaterialRepository.findById(id).orElse(null);
        if (billOfMaterial != null) {
            return billOfMaterialMapper.mapBillOfMaterial(billOfMaterial);
        }
        return null;
    }

    public List<BillOfMaterialResponse> getAll() {
        List<BillOfMaterial> all = billOfMaterialRepository.findAll();
        List<BillOfMaterialResponse> billOfMaterialResponses = new ArrayList<>();

        for (BillOfMaterial billOfMaterial : all) {
            BillOfMaterialResponse billOfMaterialResponse = billOfMaterialMapper.mapBillOfMaterial(billOfMaterial);
            billOfMaterialResponses.add(billOfMaterialResponse);
        }

        return billOfMaterialResponses;
    }
}
