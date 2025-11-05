package ma.youcode.supplyChainX.model;

import jakarta.persistence.*;
import ma.youcode.supplyChainX.shared.enums.DeliveryStatus;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Order order;

    private String vehicle;
    private String driver;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private LocalDate deliveryDate;
    private double cost;
}

