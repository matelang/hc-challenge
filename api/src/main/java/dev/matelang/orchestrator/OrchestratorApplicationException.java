package dev.matelang.orchestrator;

public class OrchestratorApplicationException extends RuntimeException {

    public OrchestratorApplicationException(String message) {
        super(message);
    }

    public OrchestratorApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
