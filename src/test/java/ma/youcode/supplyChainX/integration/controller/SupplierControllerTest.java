package ma.youcode.supplyChainX.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.youcode.supplyChainX.model.Supplier;
import ma.youcode.supplyChainX.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        supplierRepository.deleteAll();
    }

    @Test
    void createSupplier_shouldReturn200() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setName("ACME");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ACME"));
    }

    @Test
    void getSupplierById_shouldReturn200() throws Exception {
        Supplier s = supplierRepository.save(new Supplier("Dell"));

        mockMvc.perform(get("/api/suppliers/" + s.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dell"));
    }
}

