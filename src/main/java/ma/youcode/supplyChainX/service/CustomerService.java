package ma.youcode.supplyChainX.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.dto.CustomerRequest;
import ma.youcode.supplyChainX.dto.CustomerResponse;
import ma.youcode.supplyChainX.mapper.CustomerMapper;
import ma.youcode.supplyChainX.model.Customer;
import ma.youcode.supplyChainX.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse save(CustomerRequest request) {
        validateCustomer(request);

        Customer customer = customerMapper.toEntity(request);
        return customerMapper.mapToResponseDTO(customerRepository.save(customer));
    }

    public CustomerResponse update(CustomerRequest request, Long id) {
        validateCustomer(request);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());

        return customerMapper.mapToResponseDTO(customerRepository.save(customer));
    }

    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        if (!customer.getOrders().isEmpty()) {
            throw new IllegalStateException("Cannot delete customer with associated orders");
        }

        customerRepository.delete(customer);
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().
                map(customerMapper::mapToResponseDTO).collect(Collectors.toList());
    }

    public CustomerResponse findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id " + id + " not found"));

        return customerMapper.mapToResponseDTO(customer);
    }

    public List<CustomerResponse> findByName(String name) {
        List<Customer> customers = customerRepository.findByName(name.toLowerCase().trim());

        if (customers.isEmpty()) {
            throw new EntityNotFoundException("No customer with name " + name + " has been found");
        }

        return customers.stream().map(customerMapper::mapToResponseDTO).collect(Collectors.toList());
    }

    private void validateCustomer(CustomerRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (request.getCity() == null || request.getCity().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
    }
}
