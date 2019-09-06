package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Deployment {

    private String namespace;
    private String name;

    private String raw;
}
