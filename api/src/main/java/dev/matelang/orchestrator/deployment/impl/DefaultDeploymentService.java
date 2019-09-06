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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDeploymentService implements DeploymentService {

    private final static int PAGE_SIZE_LIST = 5;
    private final static int K8S_CLIENT_TIMEOUT = 30;
    private final static String K8S_CLIENT_PRETTY_PRINT = null;

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
            v1DepList = appsV1Api.listNamespacedDeployment(request.getNamespace(), K8S_CLIENT_PRETTY_PRINT,
                    request.getPaginationToken(), null,
                    null, PAGE_SIZE_LIST, null, K8S_CLIENT_TIMEOUT, false);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        DeploymentListResult result = K8sClientDtoMapper.of(v1DepList);

        return result;
    }

}
