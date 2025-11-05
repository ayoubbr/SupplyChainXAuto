package ma.youcode.supplyChainX.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private double totalCost;
    private LocalDate deliveryDate;
    private String status;
    private String vehicle;
    private String driver;
}

