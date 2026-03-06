package id.ac.ui.cs.advprog.authservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class AuthserviceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Main delegates to SpringApplication.run")
    void testMain() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AuthserviceApplication.main(new String[]{"--spring.main.web-application-type=none"});

            springApplication.verify(() ->
                    SpringApplication.run(eq(AuthserviceApplication.class), eq(new String[]{"--spring.main.web-application-type=none"})));
        }
    }
}
