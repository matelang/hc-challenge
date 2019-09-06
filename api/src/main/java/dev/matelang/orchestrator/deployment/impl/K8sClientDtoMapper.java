package dev.matelang.orchestrator.deployment.impl;

import dev.matelang.orchestrator.deployment.model.Deployment;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

public class K8sClientDtoMapper {

    static Deployment of(V1Deployment v1Deployment) {
        Deployment.DeploymentBuilder builder = Deployment.builder();

        builder
                .namespace(v1Deployment.getMetadata().getNamespace())
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
