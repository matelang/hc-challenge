package dev.matelang.orchestrator.deployment.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Data
public class DeploymentEntity {

    @Id
    @GeneratedValue
    private Long id;

    private ZonedDateTime dateTime;

    private String name;

    private String images;
}
