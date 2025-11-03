package ma.youcode.supplyChainX.mapper;

import ma.youcode.supplyChainX.dto.BillOfMaterialRequest;
import ma.youcode.supplyChainX.dto.BillOfMaterialResponse;
import ma.youcode.supplyChainX.dto.ProductResponse;
import ma.youcode.supplyChainX.dto.SupplyOrderResponse;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import ma.youcode.supplyChainX.model.Product;
import ma.youcode.supplyChainX.model.RawMaterial;
import org.springframework.stereotype.Service;

@Service
public class BillOfMaterialMapper {

    public BillOfMaterialResponse mapBillOfMaterial(BillOfMaterial billOfMaterial) {
        BillOfMaterialResponse response = new BillOfMaterialResponse();
        response.setBillOfMaterialId(billOfMaterial.getId());
        response.setRawMaterialName(billOfMaterial.getRawMaterial().getName());
        response.setRawMaterialStock((long) billOfMaterial.getRawMaterial().getStock());
        response.setProductName(billOfMaterial.getProduct().getName());
        response.setProductStock((long) billOfMaterial.getProduct().getStock());
        response.setQuantityPerProduct(billOfMaterial.getQuantity());
        return response;
    }

    public BillOfMaterial toEntity(BillOfMaterialRequest billOfMaterialRequest, RawMaterial rawMaterial, Product product) {
        BillOfMaterial billOfMaterial = new BillOfMaterial();
        billOfMaterial.setQuantity(billOfMaterialRequest.getQuantityPerProduct());
        billOfMaterial.setProduct(product);
        billOfMaterial.setRawMaterial(rawMaterial);

        return billOfMaterial;
    }

    public ProductResponse mapProduct(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCost(product.getCost());
        dto.setStock(product.getStock());
        dto.setProductionTime(product.getProductionTime());
        return dto;
    }
}
