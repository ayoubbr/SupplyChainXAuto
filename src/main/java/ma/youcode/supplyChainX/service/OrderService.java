package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.OrderRequest;
import ma.youcode.supplyChainX.dto.OrderResponse;
import ma.youcode.supplyChainX.mapper.OrderMapper;
import ma.youcode.supplyChainX.model.Customer;
import ma.youcode.supplyChainX.model.Order;
import ma.youcode.supplyChainX.model.Product;
import ma.youcode.supplyChainX.repository.CustomerRepository;
import ma.youcode.supplyChainX.repository.OrderRepository;
import ma.youcode.supplyChainX.repository.ProductRepository;
import ma.youcode.supplyChainX.shared.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderResponse save(OrderRequest orderRequest) {
        validateOrder(orderRequest);

        Order order = new Order();
        order.setStatus(OrderStatus.EN_PREPARATION);
        order.setQuantity(orderRequest.getQuantity());

        Customer customer = customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(
                () -> new EntityNotFoundException("Customer not found"));
        order.setCustomer(customer);

        Product product = productRepository.findById(orderRequest.getProductId()).orElseThrow(
                () -> new EntityNotFoundException("Product not found"));

        if (product.getStock() < orderRequest.getQuantity()) {
            throw new IllegalStateException("Product doesn't have enough stock: " +
                    product.getStock() + " - Quantity ordered: " + orderRequest.getQuantity());
        }

        product.setStock(product.getStock() - orderRequest.getQuantity());
        productRepository.save(product);
        order.setProduct(product);

        Order saved = orderRepository.save(order);
        return orderMapper.mapOrderToResponse(saved);
    }

    public OrderResponse update(Long id, OrderRequest orderRequest) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        if (existingOrder.getStatus() == OrderStatus.LIVREE) {
            throw new IllegalStateException("Cannot update an order that is already delivered");
        }


        if (orderRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        validateOrder(orderRequest);

        OrderStatus newStatus = OrderStatus.valueOf(orderRequest.getStatus());
        existingOrder.setStatus(newStatus);


        // Update customer if changed
        if (!existingOrder.getCustomer().getId().equals(orderRequest.getCustomerId())) {
            Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            existingOrder.setCustomer(customer);
        }

        Product oldProduct = existingOrder.getProduct();
        Product newProduct = oldProduct;

        Long newProductId = orderRequest.getProductId();

        // If product changed
        if (!oldProduct.getId().equals(newProductId)) {
            newProduct = productRepository.findById(newProductId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));


            // Return old product stock
            oldProduct.setStock(oldProduct.getStock() + existingOrder.getQuantity());
            productRepository.save(oldProduct);

            if (newProduct.getStock() < orderRequest.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for new product: " + newProduct.getName() +
                        " - stock: " + newProduct.getStock() + " - Quantity ordered: " + orderRequest.getQuantity());
            }

            // Deduct new product stock
            newProduct.setStock(newProduct.getStock() - orderRequest.getQuantity());
            productRepository.save(newProduct);

            existingOrder.setProduct(newProduct);
        } else {
            // If same product but quantity changed
            int quantityDiff = orderRequest.getQuantity() - existingOrder.getQuantity();
            if (quantityDiff != 0) {
                if (quantityDiff > 0 && newProduct.getStock() < quantityDiff) {
                    throw new IllegalStateException("Not enough stock for product: " + newProduct.getName());
                }
                newProduct.setStock(newProduct.getStock() - quantityDiff);
                productRepository.save(newProduct);
            }
        }

        existingOrder.setQuantity(orderRequest.getQuantity());

        return orderMapper.mapOrderToResponse(orderRepository.save(existingOrder));
    }

    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.LIVREE) {
            throw new IllegalStateException("Cannot cancel an order that is already delivered");
        }

        order.setStatus(OrderStatus.ANNULEE);

        order.getProduct().setStock(order.getProduct().getStock() + order.getQuantity());
        productRepository.save(order.getProduct());

        Order cancelled = orderRepository.save(order);

        return orderMapper.mapOrderToResponse(cancelled);
    }

    public List<OrderResponse> getAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        return orderMapper.mapOrderToResponse(order);
    }

    private void validateOrder(OrderRequest orderRequest) {
        if (orderRequest.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (orderRequest.getCustomerId() == null || orderRequest.getCustomerId() == 0) {
            throw new IllegalArgumentException("Customer id must be set");
        }
        if (orderRequest.getProductId() == null || orderRequest.getProductId() == 0) {
            throw new IllegalArgumentException("Product id must be set");
        }

        try {
            if (orderRequest.getStatus() != null) {
                OrderStatus.valueOf(orderRequest.getStatus());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + orderRequest.getStatus());
        }
    }
}
