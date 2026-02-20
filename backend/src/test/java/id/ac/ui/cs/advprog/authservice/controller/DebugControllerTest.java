package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.dto.DebugEventRequest;
import id.ac.ui.cs.advprog.authservice.model.IntegrationEvent;
import id.ac.ui.cs.advprog.authservice.service.IntegrationDebugService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DebugController Unit Tests")
class DebugControllerTest {

    @Mock
    private IntegrationDebugService integrationDebugService;

    private DebugController debugController;

    @BeforeEach
    void setUp() {
        debugController = new DebugController(integrationDebugService);
    }

    @Test
    @DisplayName("Integration endpoint returns status payload")
    void testIntegration() {
        when(integrationDebugService.integrationStatus()).thenReturn(Map.of("backend", "up"));

        ResponseEntity<Map<String, Object>> response = debugController.integration();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("up", response.getBody().get("backend"));
    }

    @Test
    @DisplayName("Create event returns 201")
    void testCreateEvent() {
        DebugEventRequest request = new DebugEventRequest();
        request.setSource("frontend-test");

        IntegrationEvent event = new IntegrationEvent("frontend-test");
        when(integrationDebugService.createEvent("frontend-test")).thenReturn(event);

        ResponseEntity<Map<String, Object>> response = debugController.createEvent(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("frontend-test", response.getBody().get("source"));
    }

    @Test
    @DisplayName("List events returns latest event payload")
    void testLatestEvents() {
        when(integrationDebugService.latestEvents(10)).thenReturn(List.of(new IntegrationEvent("frontend-test")));

        ResponseEntity<List<Map<String, Object>>> response = debugController.latestEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
