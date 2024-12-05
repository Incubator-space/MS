package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllerIT extends BaseIntegrationTest {

    @MockBean
    UserService userService;

    private UserRequest validUserRequest;
    private static final String USERS_API_URL = "/api/users";
    private static final UUID USER_ID = UUID.fromString("7a44bde9-80c7-402f-b9c3-613423623068");


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        validUserRequest = new UserRequest("testUser", "test@example.com", "password123", "John", "Doe");
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createUserTestWithModerator() throws Exception {
        mvc.perform(requestWithContent(post(USERS_API_URL), validUserRequest))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createInvalidNameUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("S", "test@example.com", "password123", "John", "Doe");
        mvc.perform(requestWithContent(post(USERS_API_URL), userRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createUserTestEmailNotBlank() throws Exception {
        UserRequest userRequest = new UserRequest("username", "", "password", "firstName", "lastName");
        mvc.perform(requestWithContent(post(USERS_API_URL), userRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAnonymousUserTest() throws Exception {
        mvc.perform(requestWithContent(post(USERS_API_URL), validUserRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createUserRoleTest() throws Exception {
        mvc.perform(requestWithContent(post(USERS_API_URL), validUserRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getUserByIdTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/hello", USER_ID);
        mvc.perform(requestToJson(requestBuilder))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void testHello() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/users/hello"))
                .andExpect(status().isOk());

    }
}


