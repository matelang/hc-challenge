package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

@Value
public class DeploymentCreationRequest {

    @NotEmpty
    private String namespace;

    @NotEmpty
    private String name;

    @NotNull
    @Size(min = 1)
    private List<Container> containers;

    @Min(1)
    @Max(100)
    @NotNull
    private Integer replicas;

    @Value
    @Builder
    public static class Container {
        @NotEmpty
        private String name;

        @NotEmpty
        private String image;

        private List<Port> ports = Collections.emptyList();

        @Value
        @Builder
        public static class Port {
            private Integer containerPort;
            private Integer hostPort;
        }
    }
}
