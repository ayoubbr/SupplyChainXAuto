package ma.youcode.supplyChainX.repository;

import ma.youcode.supplyChainX.model.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
}
