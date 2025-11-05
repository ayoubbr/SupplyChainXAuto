package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.DeliveryRequest;
import ma.youcode.supplyChainX.dto.DeliveryResponse;
import ma.youcode.supplyChainX.mapper.DeliveryMapper;
import ma.youcode.supplyChainX.model.Delivery;
import ma.youcode.supplyChainX.model.Order;
import ma.youcode.supplyChainX.repository.DeliveryRepository;
import ma.youcode.supplyChainX.repository.OrderRepository;
import ma.youcode.supplyChainX.shared.enums.DeliveryStatus;
import ma.youcode.supplyChainX.shared.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryMapper deliveryMapper;

    public DeliveryResponse createDelivery(DeliveryRequest request) {
        if (request.getCostPerKm() < 0) {
            throw new EntityNotFoundException("Cost per km cannot be less than zero");
        }
        if (request.getDistanceKm() <= 0) {
            throw new EntityNotFoundException("Distance km cannot be less than or equal to zero");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + request.getOrderId()));

        if (order.getStatus().equals(OrderStatus.ANNULEE)) {
            throw new EntityNotFoundException("Cannot create a delivery for an Order that has been annulled");
        }
        if (order.getDelivery() != null) {
            throw new IllegalStateException("A delivery already exists for this order");
        }

        order.setStatus(OrderStatus.LIVREE);
        orderRepository.save(order);

        double totalCost = request.getDistanceKm() * request.getCostPerKm();

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setStatus(DeliveryStatus.PLANIFIEE);
        delivery.setCost(totalCost);
        delivery.setDeliveryDate(request.getDeliveryDate());
        delivery.setDriver(request.getDriver());
        delivery.setVehicle(request.getVehicle());

        Delivery saved = deliveryRepository.save(delivery);

        return deliveryMapper.mapToResponse(saved);
    }
}

