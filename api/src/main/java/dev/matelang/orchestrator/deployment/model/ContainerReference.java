package dev.matelang.orchestrator.deployment.model;

import lombok.Value;

@Value
public class ContainerReference {
    private String registry;
    private String image;
    private String version;
}
