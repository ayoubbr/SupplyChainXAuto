package ma.youcode.supplyChainX.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProductionOrderResponse {
    private Long id;
    private String status;
    private int quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private double productionEstimatedTime;

    private Long productId;
    private String productName;
    private double productCost;

    private List<BillOfMaterialResponse> billOfMaterials;

    @Data
    public static class BillOfMaterialResponse {
        private Long rawMaterialId;
        private String rawMaterialName;
        private int quantityPerUnit;
        private int totalQuantityNeeded;
        private int currentStock;
    }
}

