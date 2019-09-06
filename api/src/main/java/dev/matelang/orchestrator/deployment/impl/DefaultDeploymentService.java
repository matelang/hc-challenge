package dev.matelang.orchestrator.deployment.impl;

import dev.matelang.orchestrator.deployment.DeploymentService;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1ServiceList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDeploymentService implements DeploymentService {

    private final static int PAGE_SIZE_LIST = 1;

    private final CoreV1Api coreV1Api;
    private final AppsV1Api appsV1Api;

    @Override
    public DeploymentCreationResult createDeployment(DeploymentCreationRequest request) {
        return DeploymentCreationResult.builder()
                .status("cool")
                .build();
    }

    @Override
    public DeploymentListResult listDeployments(DeploymentListRequest request) {
        V1DeploymentList v1DepList;

        try {
            v1DepList = appsV1Api.listNamespacedDeployment(request.getNamespace(), null, request.getPaginationToken(), null,
                    null, PAGE_SIZE_LIST, null, 30, false);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        DeploymentListResult result = DtoModelMapper.of(v1DepList);

        return result;
    }

}
