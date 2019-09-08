package dev.matelang.orchestrator;

import dev.matelang.orchestrator.deployment.impl.DefaultDeploymentService;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import dev.matelang.orchestrator.exception.OrchestratorApplicationException;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1DeploymentList;
import io.kubernetes.client.models.V1DeploymentSpec;
import io.kubernetes.client.models.V1DeploymentStatus;
import io.kubernetes.client.models.V1ListMeta;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1PodTemplateSpec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDeploymentServiceTest {

    private static final V1Deployment DUMMY_DEPLOYMENT =
            new V1Deployment()
                    .metadata(new V1ObjectMeta().uid("uid"))
                    .status(new V1DeploymentStatus())
                    .spec(new V1DeploymentSpec().template(new V1PodTemplateSpec()
                                    .spec(new V1PodSpec().containers(Collections.singletonList(
                                            new V1Container()
                                            )
                                            )
                                    )
                            )
                    );

    private static final DeploymentCreationRequest DUMMY_DEPLOYMENT_CREATION_REQUEST =
            DeploymentCreationRequest.builder()
                    .namespace("default")
                    .name("test")
                    .containers(List.of(DeploymentCreationRequest.Container.builder()
                            .image("testimg:0.0.1")
                            .name("test")
                            .build()))
                    .replicas(10)
                    .build();

    private AppsV1Api appsV1Api;
    private DefaultDeploymentService victim;

    @Before
    public void init() {
        appsV1Api = mock(AppsV1Api.class);
        victim = new DefaultDeploymentService(appsV1Api);
    }

    @Test
    public void listHappyWithoutPagingToken() {
        V1DeploymentList v1DeploymentList = new V1DeploymentList()
                .items(List.of(DUMMY_DEPLOYMENT, DUMMY_DEPLOYMENT, DUMMY_DEPLOYMENT))
                .metadata(new V1ListMeta()._continue(null));

        try {
            when(appsV1Api.listNamespacedDeployment(anyString(), isNull(), isNull(),
                    isNull(), isNull(), anyInt(), isNull(), anyInt(), anyBoolean()))
                    .thenReturn(v1DeploymentList);
        } catch (ApiException e) {
            fail(e.getMessage());
        }

        DeploymentListResult res =
                victim.listDeployments(new DeploymentListRequest("default", null));
        assertFalse(res.isHasMore());
        assertEquals(res.getDeployments().size(), v1DeploymentList.getItems().size());
    }

    @Test
    public void listHappyWithPagingToken() {
        V1DeploymentList v1DeploymentList = new V1DeploymentList()
                .items(List.of(DUMMY_DEPLOYMENT, DUMMY_DEPLOYMENT, DUMMY_DEPLOYMENT))
                .metadata(new V1ListMeta()._continue("token2"));

        try {
            when(appsV1Api.listNamespacedDeployment(anyString(), isNull(), anyString(),
                    isNull(), isNull(), anyInt(), isNull(), anyInt(), anyBoolean()))
                    .thenReturn(v1DeploymentList);
        } catch (ApiException e) {
            fail(e.getMessage());
        }

        DeploymentListResult res = victim.listDeployments(new DeploymentListRequest("default", "token"));
        assertTrue(res.isHasMore());
        assertEquals(res.getDeployments().size(), v1DeploymentList.getItems().size());
        assertEquals(res.getPaginationToken(), "token2");
    }

    @Test(expected = OrchestratorApplicationException.class)
    public void listK8sError() {
        try {
            when(appsV1Api.listNamespacedDeployment(anyString(), isNull(), isNull(),
                    isNull(), isNull(), anyInt(), isNull(), anyInt(), anyBoolean()))
                    .thenThrow(new ApiException());
        } catch (ApiException e) {
            fail(e.getMessage());
        }

        victim.listDeployments(new DeploymentListRequest("default", null));
    }

    @Test
    public void createHappy() {
        try {
            when(appsV1Api.createNamespacedDeployment(anyString(), any(V1Deployment.class),
                    isNull(), isNull(), isNull())).thenReturn(DUMMY_DEPLOYMENT);
        } catch (ApiException e) {
            fail(e.getMessage());
        }

        DeploymentCreationResult res = victim.createDeployment(DUMMY_DEPLOYMENT_CREATION_REQUEST);
        assertEquals(res.getUid(), "uid");
    }

    @Test(expected = OrchestratorApplicationException.class)
    public void createK8sError() {
        try {
            when(appsV1Api.createNamespacedDeployment(anyString(), any(V1Deployment.class),
                    isNull(), isNull(), isNull())).thenThrow(new ApiException());
        } catch (ApiException e) {
            fail(e.getMessage());
        }

        victim.createDeployment(DUMMY_DEPLOYMENT_CREATION_REQUEST);
    }

}
