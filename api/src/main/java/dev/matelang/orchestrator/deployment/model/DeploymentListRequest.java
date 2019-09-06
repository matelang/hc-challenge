package dev.matelang.orchestrator.deployment.model;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class DeploymentListRequest {
    @NotEmpty
    private String namespace;

    private String paginationToken;
}
