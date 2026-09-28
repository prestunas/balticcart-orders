package BalticCart.Orders.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOrders_returnsSeededOrdersSortedByCreatedAtDescending() throws Exception {
        String responseBody = mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<OrderResponse> orders = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderResponse.class));

        assertThat(orders).hasSize(12);

        for (int i = 0; i < orders.size() - 1; i++) {
            Instant current = orders.get(i).createdAt();
            Instant next = orders.get(i + 1).createdAt();
            assertThat(current).isAfterOrEqualTo(next);
        }
    }
}
