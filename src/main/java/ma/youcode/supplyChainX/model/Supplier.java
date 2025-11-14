package ma.youcode.supplyChainX.model;

import jakarta.persistence.*;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.youcode.supplyChainX.model.SupplyOrder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;
    private double rating;
    private int leadTime;

    @OneToMany(mappedBy = "supplier")
    private List<SupplyOrder> supplyOrders;

    public Supplier(String name) {
        this.name = name;
    }

    // Getters & Setters
}

