package dev.matelang.orchestrator.deployment;

import dev.matelang.orchestrator.deployment.model.Deployment;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/deployments")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@Slf4j
@RequiredArgsConstructor
public class DeploymentController {

    private static final String QUERY_PARAM_NAMESPACE = "namespace";
    private static final String QUERY_PARAM_PAGINATION_TOKEN = "paginationToken";
    private static final String QUERY_PARAM_PAGE_SIZE = "pageSize";

    private final DeploymentService deploymentService;

    @PostMapping(consumes = {"application/json"})
    public DeploymentCreationResult createDeployment(@RequestBody @Valid DeploymentCreationRequest req) {
        return deploymentService.createDeployment(req);
    }

    @GetMapping(produces = {"application/hal+json"})
    public Resources<Deployment> listDeployment(@Valid DeploymentListRequest req) {
        DeploymentListResult result = deploymentService.listDeployments(req);

        List<Link> links = new ArrayList<>();
        links.add(
                new Link(linkTo(methodOn(DeploymentController.class).listDeployment(req))
                        .toUriComponentsBuilder()
                        .queryParam(QUERY_PARAM_NAMESPACE, req.getNamespace())
                        .queryParam(QUERY_PARAM_PAGINATION_TOKEN, req.getPaginationToken())
                        .build().toUri().toASCIIString(),
                        Link.REL_SELF));

        if (result.isHasMore()) {
            links.add(
                    new Link(linkTo(methodOn(DeploymentController.class).listDeployment(req))
                            .toUriComponentsBuilder()
                            .queryParam(QUERY_PARAM_NAMESPACE, req.getNamespace())
                            .queryParam(QUERY_PARAM_PAGINATION_TOKEN, result.getPaginationToken())
                            .build().toUri().toASCIIString(),
                            Link.REL_NEXT));
        }

        return new Resources<>(result.getDeployments(), links);
    }

}
