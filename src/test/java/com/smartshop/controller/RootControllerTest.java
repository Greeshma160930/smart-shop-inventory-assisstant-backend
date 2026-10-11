package com.smartshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RootControllerTest {

    private RootController rootController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        rootController = new RootController();
        mockMvc = MockMvcBuilders.standaloneSetup(rootController).build();
    }

    @Test
    @DisplayName("Direct method call should return HTTP 200 with status UP and running message")
    void shouldReturnUpStatusAndMessageDirectly() {
        ResponseEntity<Map<String, Object>> response = rootController.root();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("Smart Shop Inventory Assistant backend is running", response.getBody().get("message"));
    }

    @Test
    @DisplayName("GET / via MockMvc should return 200 OK and expected JSON body")
    void shouldReturn200AndExpectedJsonViaMockMvc() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Smart Shop Inventory Assistant backend is running"));
    }
}
