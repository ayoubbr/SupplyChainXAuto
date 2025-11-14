package ma.youcode.supplyChainX.model;

import jakarta.persistence.*;
import lombok.*;
import ma.youcode.supplyChainX.model.BillOfMaterial;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "raw_materials")
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;
    private int stockMin;
    private String unit;

    @ManyToMany
    @JoinTable(
            name = "rawmaterial_supplier",
            joinColumns = @JoinColumn(name = "rawmaterial_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    @OneToMany(mappedBy = "rawMaterial")
    private List<BillOfMaterial> billOfMaterials;

    @OneToMany(mappedBy = "rawMaterial")
    private List<SupplyOrderRawMaterial> supplyOrderRawMaterials;

    public RawMaterial(String name, int stock, int stockMin, String unit) {
        this.name = name;
        this.stock = stock;
        this.stockMin = stockMin;
        this.unit = unit;
    }
}
