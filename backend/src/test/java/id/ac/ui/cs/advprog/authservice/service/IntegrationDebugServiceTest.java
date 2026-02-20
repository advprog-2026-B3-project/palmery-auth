package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.model.IntegrationEvent;
import id.ac.ui.cs.advprog.authservice.repo.IntegrationEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntegrationDebugService Unit Tests")
class IntegrationDebugServiceTest {

    @Mock
    private IntegrationEventRepository integrationEventRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IntegrationDebugService integrationDebugService;

    @Test
    @DisplayName("Integration status shows backend and database up")
    void testIntegrationStatus() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        when(integrationEventRepository.count()).thenReturn(3L);

        Map<String, Object> result = integrationDebugService.integrationStatus();

        assertEquals("up", result.get("backend"));
        assertNotNull(result.get("database"));
        assertEquals(3L, result.get("event_count"));
    }

    @Test
    @DisplayName("Create event uses default source when empty")
    void testCreateEventWithDefaultSource() {
        IntegrationEvent savedEvent = new IntegrationEvent("frontend-debug");
        when(integrationEventRepository.save(any(IntegrationEvent.class))).thenReturn(savedEvent);

        IntegrationEvent result = integrationDebugService.createEvent("");

        assertEquals("frontend-debug", result.getSource());
    }
}
