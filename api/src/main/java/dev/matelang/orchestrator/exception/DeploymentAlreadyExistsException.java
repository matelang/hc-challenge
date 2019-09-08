package dev.matelang.orchestrator.exception;

public class DeploymentAlreadyExistsException extends OrchestratorApplicationException {

    public DeploymentAlreadyExistsException(String message) {
        super(message);
    }

}
