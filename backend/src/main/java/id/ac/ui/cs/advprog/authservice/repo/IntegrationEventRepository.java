package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationEventRepository extends JpaRepository<IntegrationEvent, Long> {
}
