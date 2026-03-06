package id.ac.ui.cs.advprog.authservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HomeController Unit Tests")
class HomeControllerTest {

    private final HomeController controller = new HomeController();

    @Test
    @DisplayName("Home returns service and docs metadata")
    void testHome() {
        Map<String, String> result = controller.home();

        assertEquals("Palmery Auth Service", result.get("service"));
        assertEquals("/api/auth/info", result.get("docs"));
    }
}
