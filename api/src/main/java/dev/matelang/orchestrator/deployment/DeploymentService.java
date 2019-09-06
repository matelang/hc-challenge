package dev.matelang.orchestrator.deployment;

import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;

public interface DeploymentService {

    DeploymentCreationResult createDeployment(DeploymentCreationRequest request);

    DeploymentListResult listDeployments(DeploymentListRequest request);
}
