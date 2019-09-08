package dev.matelang.orchestrator.deployment.repo;

import dev.matelang.orchestrator.deployment.entity.DeploymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentRepository extends JpaRepository<DeploymentEntity, Long> {

}
