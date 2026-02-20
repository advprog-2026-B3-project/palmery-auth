package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.model.IntegrationEvent;
import id.ac.ui.cs.advprog.authservice.repo.IntegrationEventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class IntegrationDebugService {

    private final IntegrationEventRepository integrationEventRepository;
    private final JdbcTemplate jdbcTemplate;

    public IntegrationDebugService(
            IntegrationEventRepository integrationEventRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.integrationEventRepository = integrationEventRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> integrationStatus() {
        long startedAt = System.nanoTime();
        Integer ping = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        long dbLatencyMs = (System.nanoTime() - startedAt) / 1_000_000;

        return Map.of(
                "backend", "up",
                "database", Map.of(
                        "status", ping != null && ping == 1 ? "up" : "unknown",
                        "ping", ping,
                        "latency_ms", dbLatencyMs
                ),
                "event_count", integrationEventRepository.count(),
                "timestamp", Instant.now().toString()
        );
    }

    public IntegrationEvent createEvent(String source) {
        String normalizedSource = source == null || source.isBlank() ? "frontend-debug" : source;
        return integrationEventRepository.save(new IntegrationEvent(normalizedSource));
    }

    public List<IntegrationEvent> latestEvents(int limit) {
        return integrationEventRepository.findAll().stream()
                .sorted(Comparator.comparing(IntegrationEvent::getCreatedAt).reversed())
                .limit(limit)
                .toList();
    }
}
