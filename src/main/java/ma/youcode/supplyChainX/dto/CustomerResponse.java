package ma.youcode.supplyChainX.dto;

import lombok.Getter;
import lombok.Setter;
import ma.youcode.supplyChainX.model.Order;

import java.util.List;

@Getter
@Setter
public class CustomerResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private List<Order> orders;
}
