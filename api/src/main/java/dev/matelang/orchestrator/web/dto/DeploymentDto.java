package dev.matelang.orchestrator.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeploymentDto {

    private String namespace;
    private String name;
}
