package ma.youcode.supplyChainX.mapper;

import ma.youcode.supplyChainX.dto.DeliveryResponse;
import ma.youcode.supplyChainX.model.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponse mapToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setOrderId(delivery.getOrder().getId());
        response.setTotalCost(delivery.getCost());
        response.setDeliveryDate(delivery.getDeliveryDate());
        response.setDriver(delivery.getDriver());
        response.setStatus(delivery.getStatus().name());
        response.setVehicle(delivery.getVehicle());
        return response;
    }
}
