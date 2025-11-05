package ma.youcode.supplyChainX.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private int quantity;
    private double productTotalPrice;
    private String status;
    private CustomerResponse customer;
    private ProductResponse product;

    @Getter
    @Setter
    public static class ProductResponse {
        private Long id;
        private String name;
        private double cost;
        private int productionTime;
        private int stock;
    }
}
