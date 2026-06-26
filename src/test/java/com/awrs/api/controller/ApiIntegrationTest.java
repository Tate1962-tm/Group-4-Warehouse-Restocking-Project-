package com.awrs.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void login() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"manager\",\"password\":\"manager123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        authToken = "Bearer " + body.get("token").asText();
    }

    @Test
    void loginWithInvalidCredentialsReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"manager\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listItemsRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listItemsWithAuth() throws Exception {
        mockMvc.perform(get("/api/items").header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").exists());
    }

    @Test
    void receiveAndFulfillInventoryWorkflow() throws Exception {
        mockMvc.perform(post("/api/inventory/receive")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemSku":"SKU-001","locationId":"WH1-A1-S1-B1","quantity":100}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100));

        mockMvc.perform(post("/api/inventory/fulfill")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemSku":"SKU-001","locationId":"WH1-A1-S1-B1","quantity":95}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void restockTaskCreatedWhenBelowThreshold() throws Exception {
        mockMvc.perform(post("/api/inventory/receive")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemSku":"SKU-001","locationId":"WH1-A1-S1-B1","quantity":5}
                                """))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/restock/evaluate")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemSku":"SKU-001","locationId":"WH1-A1-S1-B1"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode task = objectMapper.readTree(result.getResponse().getContentAsString());
        String taskId = task.get("id").asText();
        assertTrue(task.get("priority").asInt() > 0);

        mockMvc.perform(post("/api/restock/tasks/" + taskId + "/complete")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void adjustInventoryRequiresManagerOrAdmin() throws Exception {
        MvcResult workerLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"worker\",\"password\":\"worker123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String workerToken = "Bearer " + objectMapper.readTree(workerLogin.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(post("/api/inventory/adjust")
                        .header("Authorization", workerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"itemSku":"SKU-001","locationId":"WH1-A1-S1-B1","newQuantity":10,"reason":"count"}
                                """))
                .andExpect(status().isForbidden());
    }
}
