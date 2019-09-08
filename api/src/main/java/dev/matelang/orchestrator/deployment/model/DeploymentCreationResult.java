package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeploymentCreationResult {
    private String uid;
}
