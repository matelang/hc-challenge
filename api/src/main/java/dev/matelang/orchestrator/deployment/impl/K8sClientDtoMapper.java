package dev.matelang.orchestrator.deployment.impl;

import dev.matelang.orchestrator.deployment.model.Deployment;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1ContainerPort;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1DeploymentSpec;
import io.kubernetes.client.models.V1LabelSelector;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplateSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class K8sClientDtoMapper {

    private static final String DEPLOYMENT_API_VERSION = "apps/v1";
    private static final String DEPLOYMENT_KIND = "Deployment";

    private static final String LABEL_APP = "app";

    static V1Deployment of(DeploymentCreationRequest request) {
        V1Deployment v1Deployment = new V1Deployment();

        V1PodSpec v1PodSpec = new V1PodSpec()
                .containers(
                        request.getContainers().stream()
                                .map(c -> new V1Container()
                                        .name(c.getName())
                                        .image(c.getImage())
                                        .ports(c.getPorts().stream()
                                                .map(p -> new V1ContainerPort()
                                                        .containerPort(p.getContainerPort())
                                                        .hostPort(p.getHostPort())
                                                )
                                                .collect(Collectors.toList())
                                        )
                                )
                                .collect(Collectors.toList())
                );

        V1DeploymentSpec v1DeploymentSpec = new V1DeploymentSpec()
                .replicas(request.getReplicas())
                .selector(new V1LabelSelector().matchLabels(Map.of(LABEL_APP, request.getName())))
                .template(
                        new V1PodTemplateSpec()
                                .metadata(new V1ObjectMeta().labels(Map.of(LABEL_APP, request.getName())))
                                .spec(v1PodSpec)
                );

        v1Deployment
                .apiVersion(DEPLOYMENT_API_VERSION)
                .kind(DEPLOYMENT_KIND)
                .metadata(new V1ObjectMeta()
                        .name(request.getName())
                        .labels(Map.of(LABEL_APP, request.getName()))
                )
                .spec(v1DeploymentSpec);


        log.info("V1DEPL={}",v1Deployment);
        return v1Deployment;
    }

    static Deployment of(V1Deployment v1Deployment) {
        Deployment.DeploymentBuilder builder = Deployment.builder();

        builder.namespace(v1Deployment.getMetadata().getNamespace())
                .name(v1Deployment.getMetadata().getName());

        builder.status(Deployment.Status.builder()
                .availableReplicas(v1Deployment.getStatus().getAvailableReplicas())
                .readyReplicas(v1Deployment.getStatus().getReadyReplicas())
                .replicas(v1Deployment.getStatus().getReplicas())
                .unavailableReplicas(v1Deployment.getStatus().getUnavailableReplicas())
                .updatedReplicas(v1Deployment.getStatus().getUpdatedReplicas())
                .build()
        );

        Deployment.PodSpec podSpec = Deployment.PodSpec.builder()
                .containers(
                        v1Deployment.getSpec().getTemplate().getSpec().getContainers().stream()
                                .map(v1c -> Deployment.Container.builder()
                                        .args(v1c.getArgs())
                                        .command(v1c.getCommand())
                                        .image(v1c.getImage())
                                        .imagePullPolicy(v1c.getImagePullPolicy())
                                        .name(v1c.getName())
                                        .workingDir(v1c.getWorkingDir())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();

        builder.spec(Deployment.Spec.builder()
                .minReadySeconds(v1Deployment.getSpec().getMinReadySeconds())
                .paused(v1Deployment.getSpec().isPaused())
                .replicas(v1Deployment.getSpec().getReplicas())
                .revisionHistoryLimit(v1Deployment.getSpec().getRevisionHistoryLimit())
                .podTemplateSpec(Deployment.PodTemplateSpec.builder()
                        .podSpec(podSpec)
                        .build()
                )
                .build()
        );

        return builder.build();
    }

    static DeploymentListResult of(V1DeploymentList v1DeploymentList) {
        DeploymentListResult.DeploymentListResultBuilder builder = DeploymentListResult.builder();

        builder.deployments(
                v1DeploymentList.getItems()
                        .stream()
                        .map(K8sClientDtoMapper::of)
                        .collect(Collectors.toList())
        );

        String continueToken = v1DeploymentList.getMetadata().getContinue();
        builder.hasMore(!StringUtils.isEmpty(continueToken));
        builder.paginationToken(continueToken);

        return builder.build();
    }
}
