package dev.matelang.orchestrator.deployment.model;

import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Value
public class DeploymentListRequest {

    private static final int MINIMUM_PAGE_SIZE = 1;
    private static final int MAXIMUM_PAGE_SIZE = 20;
    private static final int DEFAULT_PAGE_SIZE = 3;

    @NotEmpty
    private String namespace;

    private String paginationToken;

    @Min(MINIMUM_PAGE_SIZE)
    @Max(MAXIMUM_PAGE_SIZE)
    private Integer pageSize = DEFAULT_PAGE_SIZE;
}
