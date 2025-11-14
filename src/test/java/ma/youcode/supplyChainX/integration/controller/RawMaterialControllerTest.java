package ma.youcode.supplyChainX.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.youcode.supplyChainX.model.RawMaterial;
import ma.youcode.supplyChainX.repository.RawMaterialRepository;
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
class RawMaterialControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        rawMaterialRepository.deleteAll();
    }

    @Test
    void createRawMaterial_shouldReturn200() throws Exception {
        RawMaterial material = new RawMaterial("Steel", 120, 100, "kg");

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Steel"));
    }

    @Test
    void getBelowStock_shouldReturn200() throws Exception {
        rawMaterialRepository.save(new RawMaterial("Copper", 3, 10, "kg"));
        rawMaterialRepository.save(new RawMaterial("Wood", 50, 20, "kg"));

        mockMvc.perform(get("/api/raw-materials/below-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Copper"));
    }
}

