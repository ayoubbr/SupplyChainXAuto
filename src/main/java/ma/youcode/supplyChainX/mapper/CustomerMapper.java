package ma.youcode.supplyChainX.mapper;

import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.CustomerRequest;
import ma.youcode.supplyChainX.dto.CustomerResponse;
import ma.youcode.supplyChainX.model.Customer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerMapper {

    public CustomerResponse mapToResponseDTO(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        return response;
    }

    public Customer toEntity(CustomerRequest request) {
        Customer product = new Customer();
        product.setName(request.getName());
        product.setAddress(request.getAddress());
        product.setCity(request.getCity());
        return product;
    }

}
