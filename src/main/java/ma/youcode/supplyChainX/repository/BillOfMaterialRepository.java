package ma.youcode.supplyChainX.repository;

import ma.youcode.supplyChainX.dto.BillOfMaterialRequest;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {
    List<BillOfMaterial> findByProductId(Long id);

    BillOfMaterial findByProductIdAndRawMaterialId(Long productId, Long rawMaterialId);
}
