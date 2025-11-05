package ma.youcode.supplyChainX.model;

import jakarta.persistence.*;
import ma.youcode.supplyChainX.shared.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Product product;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;
}
