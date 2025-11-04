package ma.youcode.supplyChainX.mapper;

import ma.youcode.supplyChainX.dto.ProductionOrderResponse;
import ma.youcode.supplyChainX.model.ProductionOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionOrderMapper {

    public ProductionOrderResponse mapToResponseDTO(ProductionOrder order) {
        ProductionOrderResponse dto = new ProductionOrderResponse();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setQuantity(order.getQuantity());
        dto.setStartDate(order.getStartDate());
        dto.setEndDate(order.getEndDate());
        dto.setProductId(order.getProduct().getId());
        dto.setProductName(order.getProduct().getName());
        dto.setProductCost(order.getProduct().getCost());

        List<ProductionOrderResponse.BillOfMaterialResponse> bomResponses = order.getProduct()
                .getBillOfMaterials()
                .stream()
                .map(bom -> {
                    ProductionOrderResponse.BillOfMaterialResponse bomDto =
                            new ProductionOrderResponse.BillOfMaterialResponse();
                    bomDto.setRawMaterialId(bom.getRawMaterial().getId());
                    bomDto.setRawMaterialName(bom.getRawMaterial().getName());
                    bomDto.setQuantityPerUnit(bom.getQuantity());
                    bomDto.setTotalQuantityNeeded(bom.getQuantity() * order.getQuantity());
                    bomDto.setCurrentStock(bom.getRawMaterial().getStock());
                    return bomDto;
                })
                .toList();

        dto.setBillOfMaterials(bomResponses);
        dto.setProductionEstimatedTime(order.getQuantity() * order.getProduct().getProductionTime());

        return dto;
    }
}
