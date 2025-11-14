package ma.youcode.supplyChainX.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.youcode.supplyChainX.dto.UserRequest;
import ma.youcode.supplyChainX.model.User;
import ma.youcode.supplyChainX.repository.UserRepository;
import ma.youcode.supplyChainX.shared.enums.Role;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldReturn200() throws Exception {
        UserRequest request = new UserRequest(
                "ayoub@gmail.com",
                "123456",
                "ayoub",
                "lastname",
                Role.ADMIN);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ayoub@gmail.com"));
    }

    @Test
    void updateRole_shouldReturn200() throws Exception {
        User user = new User("t@t.com", "1234", "test", "test", Role.CHEF_PRODUCTION);
        userRepository.save(user);

        mockMvc.perform(put("/api/users/" + user.getId() + "/role")
                        .param("newRole", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}

