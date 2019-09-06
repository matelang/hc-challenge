package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DeploymentListResult {

    private List<Deployment> deployments;

    private boolean hasMore;
    private String paginationToken;
}
