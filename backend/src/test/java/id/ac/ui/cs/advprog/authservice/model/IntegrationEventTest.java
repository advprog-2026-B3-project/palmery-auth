package id.ac.ui.cs.advprog.authservice.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IntegrationEvent Model Unit Tests")
class IntegrationEventTest {

    @Test
    @DisplayName("Constructor sets source and prePersist sets createdAt")
    void testConstructorAndPrePersist() {
        IntegrationEvent event = new IntegrationEvent("frontend");

        assertNull(event.getCreatedAt());
        assertEquals("frontend", event.getSource());

        event.prePersist();

        assertNotNull(event.getCreatedAt());
    }

    @Test
    @DisplayName("prePersist does not overwrite existing createdAt")
    void testPrePersistDoesNotOverwriteCreatedAt() throws Exception {
        IntegrationEvent event = new IntegrationEvent("frontend");
        Instant fixedTimestamp = Instant.parse("2024-01-01T00:00:00Z");
        setCreatedAt(event, fixedTimestamp);

        event.prePersist();

        assertEquals(fixedTimestamp, event.getCreatedAt());
    }

    @Test
    @DisplayName("Setter updates source")
    void testSetSource() {
        IntegrationEvent event = new IntegrationEvent("initial");

        event.setSource("updated");

        assertEquals("updated", event.getSource());
    }

    private void setCreatedAt(IntegrationEvent event, Instant value) throws Exception {
        java.lang.reflect.Field createdAtField = IntegrationEvent.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(event, value);
    }
}
