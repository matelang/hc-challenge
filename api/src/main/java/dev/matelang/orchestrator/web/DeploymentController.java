package dev.matelang.orchestrator.web;

import dev.matelang.orchestrator.deployment.DeploymentService;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentCreationResult;
import dev.matelang.orchestrator.deployment.model.DeploymentListRequest;
import dev.matelang.orchestrator.deployment.model.DeploymentListResult;
import dev.matelang.orchestrator.web.dto.DeploymentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/deployments")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@Slf4j
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping
    public DeploymentCreationResult createDeployment(DeploymentCreationRequest req) {
        return deploymentService.createDeployment(req);
    }

    @GetMapping(produces = {"application/hal+json"})
    public Resources<DeploymentDto> listDeployment(DeploymentListRequest req) {
        DeploymentListResult result = deploymentService.listDeployments(req);

        log.info("Got = {}", result);

        List<DeploymentDto> deploymentList = result.getDeployments().stream()
                .map(d ->
                        DeploymentDto.builder()
                                .namespace(d.getNamespace())
                                .name(d.getName())
                                .build()
                )
                .collect(Collectors.toList());


        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(DeploymentController.class).listDeployment(req)).withSelfRel());
        if (result.isHasMore()) {
            links.add(
                    new Link(linkTo(methodOn(DeploymentController.class).listDeployment(req))
                            .toUriComponentsBuilder()
                            .queryParam("paginationToken", result.getPaginationToken())
                            .build().toUri().toASCIIString(),
                            Link.REL_NEXT));
        }

        return new Resources<>(deploymentList, links);
    }

}
