package ma.youcode.supplyChainX.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DeliveryRequest {
    private Long orderId;
    private double distanceKm;
    private double costPerKm;
    private String vehicle;
    private String driver;
    private LocalDate deliveryDate;
}

