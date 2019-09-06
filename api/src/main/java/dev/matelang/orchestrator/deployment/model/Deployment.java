package dev.matelang.orchestrator.deployment.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

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
        private Integer replicas;
        private Integer revisionHistoryLimit;
        // private V1LabelSelector selector;
        // private V1DeploymentStrategy strategy;
        private PodTemplateSpec podTemplateSpec;
    }

    @Value
    @Builder
    public static class PodTemplateSpec {

        private PodSpec podSpec;
    }

    @Value
    @Builder
    public static class PodSpec {
        private List<Container> containers;
    }

    @Value
    @Builder
    public static class Container {
        private String image;
        private String imagePullPolicy;
        private String name;
        private List<String> command;
        private List<String> args;
        private String workingDir;

//        private List<V1EnvVar> env;
//        private List<V1EnvFromSource> envFrom;
//        private V1Lifecycle lifecycle;
//        private V1Probe livenessProbe;
//        private List<V1ContainerPort> ports;
//        private V1Probe readinessProbe;
//        private V1ResourceRequirements resources;
//        private V1SecurityContext securityContext;
//        private Boolean stdin;
//        private Boolean stdinOnce;
//        private String terminationMessagePath;
//        private String terminationMessagePolicy;
//        private Boolean tty;
//        private List<V1VolumeDevice> volumeDevices;
//        private List<V1VolumeMount> volumeMounts;
    }
}
