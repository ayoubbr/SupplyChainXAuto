package ma.youcode.supplyChainX.mapper;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.ProductRequest;
import ma.youcode.supplyChainX.dto.ProductResponse;
import ma.youcode.supplyChainX.model.BillOfMaterial;
import ma.youcode.supplyChainX.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMapper {

    private final BillOfMaterialMapper billOfMaterialMapper;

    public ProductResponse mapToResponseDTO(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setStock(product.getStock());
        response.setCost(product.getCost());
        response.setProductionTime(product.getProductionTime());

        List<ProductResponse.BillOfMaterialResponseDTO> materials = product.getBillOfMaterials().stream()
                .map(bom -> {
                    ProductResponse.BillOfMaterialResponseDTO dto = new ProductResponse.BillOfMaterialResponseDTO();
                    dto.setId(bom.getId());
                    dto.setRawMaterialId(bom.getRawMaterial().getId());
                    dto.setRawMaterialName(bom.getRawMaterial().getName());
                    dto.setQuantity(bom.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());

        response.setBillOfMaterials(materials);
        return response;
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCost(request.getCost());
        product.setStock(request.getStock());
        product.setProductionTime(request.getProductionTime());
        return product;
    }

}
