package dev.matelang.orchestrator.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ErrorDto {
    private String requestUid = MDC.get("X-B3-TraceId");
    private long timestamp = Instant.now().toEpochMilli();
    private int httpStatus;
    private String message;
    private ErrorCode errorCode;

    public ErrorDto(HttpStatus httpStatus, Exception e) {
        setHttpStatus(httpStatus.value());
        setMessage(e.getMessage());
    }

    public ErrorDto(HttpStatus httpStatus, String message) {
        setHttpStatus(httpStatus.value());
        setMessage(message);
    }

    public ErrorDto(HttpStatus httpStatus, Exception e, ErrorCode errorCode) {
        setHttpStatus(httpStatus.value());
        setMessage(e.getMessage());
        setErrorCode(errorCode);
    }
}
