package dev.matelang.orchestrator.deployment.impl;

import dev.matelang.orchestrator.deployment.model.Deployment;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

public class DtoModelMapper {

    public DeploymentCreationResult of() {
        DeploymentCreationResult.DeploymentCreationResultBuilder builder = DeploymentCreationResult.builder();

        return builder.build();
    }

    static Deployment of(V1Deployment v1Deployment) {
        Deployment.DeploymentBuilder builder = Deployment.builder();

        builder.namespace(v1Deployment.getMetadata().getNamespace());
        builder.name(v1Deployment.getMetadata().getName());

        builder.raw(v1Deployment.toString());

        return builder.build();
    }

    static DeploymentListResult of(V1DeploymentList v1DeploymentList) {
        DeploymentListResult.DeploymentListResultBuilder builder = DeploymentListResult.builder();

        builder.deployments(
                v1DeploymentList.getItems()
                        .stream()
                        .map(DtoModelMapper::of)
                        .collect(Collectors.toList())
        );

        String continueToken = v1DeploymentList.getMetadata().getContinue();
        builder.hasMore(!StringUtils.isEmpty(continueToken));
        builder.paginationToken(continueToken);

        return builder.build();
    }
}
