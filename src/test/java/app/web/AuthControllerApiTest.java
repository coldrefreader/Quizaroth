package app.web;

import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenHappyFlow_testPostRegisterEndpoint() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("HelloBand")
                .password("WeHearYou2")
                .confirmPassword("WeHearYou2")
                .build();

        String registerRequestJson = objectMapper.writeValueAsString(registerRequest);

        when(userService.register(registerRequest)).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerRequestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":  \"User successfully registered!\"}"));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void givenHappyFlow_testPutEditEndpoint() throws Exception {

        UserEditRequest userEditRequest = new UserEditRequest("newFirst", "newLast", "new@example.com");
        UUID userId = UUID.randomUUID();

        when(userService.getUserIdByUsername("testUser")).thenReturn(userId);

        String userEditRequestJson = objectMapper.writeValueAsString(userEditRequest);

        mockMvc.perform(put("/v1/auth/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(userEditRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":  \"Profile successfully updated!\"}"));

        verify(userService, times(1)).getUserIdByUsername("testUser");
        verify(userService, times(1)).editUserInformation(userId, userEditRequest);
    }
}
