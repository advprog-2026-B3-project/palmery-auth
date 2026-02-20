package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.dto.DebugEventRequest;
import id.ac.ui.cs.advprog.authservice.model.IntegrationEvent;
import id.ac.ui.cs.advprog.authservice.service.IntegrationDebugService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final IntegrationDebugService integrationDebugService;

    public DebugController(IntegrationDebugService integrationDebugService) {
        this.integrationDebugService = integrationDebugService;
    }

    @GetMapping("/integration")
    public ResponseEntity<Map<String, Object>> integration() {
        return ResponseEntity.ok(integrationDebugService.integrationStatus());
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody(required = false) DebugEventRequest request) {
        String source = request == null ? null : request.getSource();
        IntegrationEvent event = integrationDebugService.createEvent(source);

        Map<String, Object> response = new HashMap<>();
        response.put("id", event.getId());
        response.put("source", event.getSource());
        response.put("created_at", event.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Map<String, Object>>> latestEvents() {
        List<Map<String, Object>> response = integrationDebugService.latestEvents(10).stream()
                .map(event -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", event.getId());
                    entry.put("source", event.getSource());
                    entry.put("created_at", event.getCreatedAt());
                    return entry;
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}
