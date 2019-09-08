package dev.matelang.orchestrator.deployment.impl;

import io.kubernetes.client.models.V1Deployment;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeploymentCreatedEvent {
    private V1Deployment deployment;
}
