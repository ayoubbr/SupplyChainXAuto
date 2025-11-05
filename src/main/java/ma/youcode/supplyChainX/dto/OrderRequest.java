package ma.youcode.supplyChainX.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long customerId;
    private Long productId;
    private int quantity;
    private String status;
}
