package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Deployment {

    private String namespace;
    private String name;
    private Status status;
    private Spec spec;

    @Value
    @Builder
    public static class Status {
        private Integer availableReplicas;
        private Integer readyReplicas;
        private Integer replicas;
        private Integer unavailableReplicas;
        private Integer updatedReplicas;

    }

    @Value
    @Builder
    public static class Spec {
        private Integer minReadySeconds;
        private Boolean paused;
        private Integer progressDeadlineSeconds;
        private Integer replicas;
        private Integer revisionHistoryLimit;
//        private V1LabelSelector selector;
//        private V1DeploymentStrategy strategy;
//        private V1PodTemplateSpec template;
    }

}
