package ma.youcode.supplyChainX.mapper;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.OrderResponse;
import ma.youcode.supplyChainX.model.Order;
import ma.youcode.supplyChainX.model.Product;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMapper {

    private final CustomerMapper customerMapper;

    public OrderResponse mapOrderToResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setQuantity(order.getQuantity());
        orderResponse.setStatus(order.getStatus().name());

        orderResponse.setProductTotalPrice(order.getQuantity() * order.getProduct().getCost());

        orderResponse.setCustomer(customerMapper.mapToResponseDTO(order.getCustomer()));
        orderResponse.setProduct(mapProductToResponse(order.getProduct()));

        return orderResponse;
    }


    public OrderResponse.ProductResponse mapProductToResponse(Product product) {
        OrderResponse.ProductResponse productResponse = new OrderResponse.ProductResponse();

        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setProductionTime(product.getProductionTime());
        productResponse.setStock(product.getStock());
        productResponse.setCost(product.getCost());

        return productResponse;
    }
}
