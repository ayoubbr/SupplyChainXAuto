package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.ProductionOrderRequest;
import ma.youcode.supplyChainX.dto.ProductionOrderResponse;
import ma.youcode.supplyChainX.mapper.ProductionOrderMapper;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import ma.youcode.supplyChainX.model.Product;
import ma.youcode.supplyChainX.model.ProductionOrder;
import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.repository.BillOfMaterialRepository;
import ma.youcode.supplyChainX.repository.ProductRepository;
import ma.youcode.supplyChainX.repository.ProductionOrderRepository;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
import ma.youcode.supplyChainX.shared.enums.ProductionOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductRepository productRepository;
    private final ProductionOrderMapper productionOrderMapper;


    public ProductionOrderResponse save(ProductionOrderRequest request) {
        Product product = validateProduct(request);
        setNewBillOfMaterials(request, product);

        ProductionOrder productionOrder = new ProductionOrder();
        productionOrder.setProduct(product);
        productionOrder.setQuantity(request.getQuantity());
        productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        productionOrder.setStartDate(request.getStartDate());
        productionOrder.setEndDate(request.getEndDate());

        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.mapToResponseDTO(savedOrder);
    }

    public ProductionOrderResponse update(ProductionOrderRequest request, Long id) {
        ProductionOrder existingOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Production order not found with ID: " + id));

        Product newProduct = validateProduct(request);

        if (!existingOrder.getProduct().getId().equals(newProduct.getId())
                || existingOrder.getQuantity() != request.getQuantity()) {

            // if bom changed, return the old quantity to stock of raw material
            restoreOldBillOfMaterials(existingOrder);

            // set new boms
            setNewBillOfMaterials(request, newProduct);

            existingOrder.setProduct(newProduct);
            existingOrder.setQuantity(request.getQuantity());
        }

        existingOrder.setStartDate(request.getStartDate());
        existingOrder.setEndDate(request.getEndDate());
        existingOrder.setStatus(request.getStatus() != null
                ? ProductionOrderStatus.valueOf(request.getStatus())
                : existingOrder.getStatus()
        );

        if (ProductionOrderStatus.valueOf(request.getStatus()).equals(ProductionOrderStatus.TERMINE)) {
            newProduct.setStock(newProduct.getStock() + request.getQuantity());
            productRepository.save(newProduct);
        }

        ProductionOrder updated = productionOrderRepository.save(existingOrder);

        return productionOrderMapper.mapToResponseDTO(updated);
    }

    public void delete(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Production order not found with ID: " + id));

        if (order.getStatus() == ProductionOrderStatus.TERMINE ||
                order.getStatus() == ProductionOrderStatus.EN_PRODUCTION) {
            throw new IllegalStateException("Cannot delete a production order that is already in progress or completed");
        }

        // Restore raw materials
        restoreOldBillOfMaterials(order);

        productionOrderRepository.delete(order);
    }

    public void cancel(Long id) {
        ProductionOrder order = productionOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Production order not found with ID: " + id));

        if (order.getStatus() == ProductionOrderStatus.TERMINE ||
                order.getStatus() == ProductionOrderStatus.EN_PRODUCTION) {
            throw new IllegalStateException("Cannot cancel a production order that has already started");
        }

        // Restore raw materials
        restoreOldBillOfMaterials(order);

        order.setStatus(ProductionOrderStatus.BLOQUE);

        productionOrderRepository.save(order);
    }

    public List<ProductionOrderResponse> getAll() {
        return productionOrderRepository.findAll()
                .stream()
                .map(productionOrderMapper::mapToResponseDTO).toList();
    }

    public ProductionOrderResponse getById(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Production order with id " + id + " not found"));

        return productionOrderMapper.mapToResponseDTO(productionOrder);
    }

    private static List<BillOfMaterial> getBillOfMaterials(
            ProductionOrderRequest request, Product product) {
        List<BillOfMaterial> bomList = product.getBillOfMaterials();

        if (bomList.isEmpty()) {
            throw new IllegalStateException("No Bill of Materials found for product: " + product.getName());
        }

        for (BillOfMaterial bom : bomList) {
            RawMaterial raw = bom.getRawMaterial();
            int requiredQty = bom.getQuantity() * request.getQuantity();

            if (raw.getStock() < requiredQty) {
                throw new IllegalStateException("Insufficient stock for raw material: " + raw.getName() +
                        " (required: " + requiredQty + ", available: " + raw.getStock() + ")");
            }
        }
        return bomList;
    }

    private void setNewBillOfMaterials(ProductionOrderRequest request, Product product) {
        List<BillOfMaterial> bomList = getBillOfMaterials(request, product);

        for (BillOfMaterial bom : bomList) {
            RawMaterial raw = bom.getRawMaterial();
            int requiredQty = bom.getQuantity() * request.getQuantity();
            raw.setStock(raw.getStock() - requiredQty);
            rawMaterialRepository.save(raw);
        }
    }

    private void restoreOldBillOfMaterials(ProductionOrder order) {
        List<BillOfMaterial> bomList = order.getProduct().getBillOfMaterials();
        for (BillOfMaterial bom : bomList) {
            RawMaterial raw = bom.getRawMaterial();
            int quantityToRestore = bom.getQuantity() * order.getQuantity();
            raw.setStock(raw.getStock() + quantityToRestore);
            rawMaterialRepository.save(raw);
        }
    }

    private Product validateProduct(ProductionOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Production order quantity must be greater than zero");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Production order start date cannot be in the past");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Production order end date cannot be before start date");
        }

        return product;
    }

}
