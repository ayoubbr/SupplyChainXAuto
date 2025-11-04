package ma.youcode.supplyChainX.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductionOrderRequest {
    private Long productId;
    private int quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

