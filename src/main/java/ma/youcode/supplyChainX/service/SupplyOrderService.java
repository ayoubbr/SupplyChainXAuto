package ma.youcode.supplyChainX.service;

import ma.youcode.supplyChainX.dto.SupplyOrderRequest;
import ma.youcode.supplyChainX.dto.SupplyOrderResponse;
import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.model.Supplier;
import ma.youcode.supplyChainX.model.SupplyOrder;
import ma.youcode.supplyChainX.model.SupplyOrderRawMaterial;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
import ma.youcode.supplyChainX.repository.SupplierRepository;
import ma.youcode.supplyChainX.repository.SupplyOrderRepository;
import ma.youcode.supplyChainX.shared.enums.SupplyOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SupplyOrderService {

    private SupplyOrderRepository supplyOrderRepository;
    private RawMaterialRepository rawMaterialRepository;
    private SupplierRepository supplierRepository;

    private SupplyOrderRawMaterialService supplyOrderRawMaterialService;

    public SupplyOrderService(SupplyOrderRepository supplyOrderRepository,
                              RawMaterialRepository rawMaterialRepository,
                              SupplyOrderRawMaterialService supplyOrderRawMaterialService,
                              SupplierRepository supplierRepository) {
        this.supplyOrderRepository = supplyOrderRepository;
        this.rawMaterialRepository = rawMaterialRepository;
        this.supplyOrderRawMaterialService = supplyOrderRawMaterialService;
        this.supplierRepository = supplierRepository;
    }

    public SupplyOrder save(SupplyOrderRequest supplyOrderRequest) {
        if (supplyOrderRequest.getOrderDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Supply order date cannot be in the future.");
        }

        if (supplyOrderRequest.getSupplierId() == null || supplyOrderRequest.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Supply order must have a valid supplier.");
        }

        Supplier supplier = supplierRepository.findById(supplyOrderRequest.getSupplierId()).orElseThrow(() ->
                new IllegalArgumentException("Supplier with ID " + supplyOrderRequest.getSupplierId() + " does not exist."));

        SupplyOrder supplyOrder = new SupplyOrder();
        supplyOrder.setDate(supplyOrderRequest.getOrderDate());
        supplyOrder.setSupplier(supplier);
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);

        List<SupplyOrderRawMaterial> supplyOrderRawMaterials = new ArrayList<>();

        for (SupplyOrderRequest.RawMaterialQuantity rmq : supplyOrderRequest.getRawMaterials()) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(rmq.getRawMaterialId()).orElseThrow(() ->
                    new IllegalArgumentException("Raw material with ID " + rmq.getRawMaterialId() + " does not exist."));

            if (rawMaterial.getSuppliers() == null || !rawMaterial.getSuppliers().contains(supplier)) {
                throw new IllegalArgumentException("Supplier with ID " + supplier.getId() +
                        " does not supply raw material with ID " + rawMaterial.getId() + ".");
            }

            SupplyOrderRawMaterial supplyOrderRawMaterial = new SupplyOrderRawMaterial();
            supplyOrderRawMaterial.setRawMaterial(rawMaterial);
            supplyOrderRawMaterial.setQuantity(rmq.getQuantity());
            supplyOrderRawMaterial.setSupplyOrder(supplyOrder);

            supplyOrderRawMaterials.add(supplyOrderRawMaterial);
        }

        supplyOrder.setSupplyOrderRawMaterials(supplyOrderRawMaterials);

        return supplyOrderRepository.save(supplyOrder);
    }

    public List<SupplyOrder> findAll() {
        return supplyOrderRepository.findAll();
    }

    public SupplyOrder findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Supply order ID cannot be null.");
        }
        return supplyOrderRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Supply order with ID " + id + " does not exist."));
    }

    public List<SupplyOrder> findBySupplierId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Supplier ID cannot be null.");
        }
        return findAll().stream()
                .filter(order -> order.getSupplier() != null && order.getSupplier().getId().equals(id))
                .toList();
    }

    public int deleteById(Long id) {
        SupplyOrder existingOrder = supplyOrderRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Supply order with the given ID does not exist."));

        if (existingOrder.getStatus() != SupplyOrderStatus.RECUE) {
            supplyOrderRepository.deleteById(id);
            return 1;
        }

        return 0;
    }

    public SupplyOrder update(SupplyOrderRequest supplyOrder, Long id) {
        SupplyOrder existingOrder = supplyOrderRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Supply order with the given ID does not exist."));

        existingOrder.setDate(supplyOrder.getOrderDate());
        existingOrder.setStatus(SupplyOrderStatus.valueOf(supplyOrder.getStatus()));

        if (SupplyOrderStatus.valueOf(supplyOrder.getStatus()) == SupplyOrderStatus.RECUE) {
            for (SupplyOrderRawMaterial supplyOrderRawMaterial : existingOrder.getSupplyOrderRawMaterials()) {
                RawMaterial rawMaterial = supplyOrderRawMaterial.getRawMaterial();
                rawMaterial.setStock(rawMaterial.getStock() + supplyOrderRawMaterial.getQuantity());
                rawMaterialRepository.save(rawMaterial);
            }
        }

        supplyOrderRepository.save(existingOrder);
        return existingOrder;
    }

    public SupplyOrderResponse toResponse(SupplyOrder order) {
        SupplyOrderResponse dto = new SupplyOrderResponse();
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setStatus(order.getStatus().name());

        // supplier info
        SupplyOrderResponse.SupplierResponse supplier = new SupplyOrderResponse.SupplierResponse();
        supplier.setId(order.getSupplier().getId());
        supplier.setName(order.getSupplier().getName());
        supplier.setContact(order.getSupplier().getContact());
        supplier.setRating(order.getSupplier().getRating());
        supplier.setLeadTime(order.getSupplier().getLeadTime());
        dto.setSupplier(supplier);

        // raw materials with quantity
        List<SupplyOrderResponse.RawMaterialResponse> rawMaterialResponses =
                order.getSupplyOrderRawMaterials().stream()
                        .map(srm -> {
                            SupplyOrderResponse.RawMaterialResponse r = new SupplyOrderResponse.RawMaterialResponse();
                            r.setId(srm.getRawMaterial().getId());
                            r.setName(srm.getRawMaterial().getName());
                            r.setQuantity(srm.getQuantity());
                            return r;
                        })
                        .toList();

        dto.setRawMaterials(rawMaterialResponses);

        return dto;
    }

    public List<SupplyOrderResponse> toResponseList(List<SupplyOrder> orders) {
        return orders.stream().map(this::toResponse).toList();
    }
}
