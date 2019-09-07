package dev.matelang.orchestrator.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> process(Exception ex) {
        logException(ex, LogLevel.ERROR, true);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    @ExceptionHandler(OrchestratorApplicationException.class)
    public ResponseEntity<ErrorDto> process(OrchestratorApplicationException ex) {
        logException(ex, LogLevel.ERROR, true);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, ex, ErrorCode.GENERIC));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(MethodArgumentNotValidException ex) {
        logException(ex, LogLevel.INFO, false);

        return new ErrorDto(HttpStatus.BAD_REQUEST, extractMessage(ex.getBindingResult()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(MethodArgumentTypeMismatchException ex) {
        logException(ex, LogLevel.INFO, false);

        String message = "MethodArgumentTypeMismatch on name=" + ex.getName() +
                " and type=" + ex.getParameter().getParameterType();

        return new ErrorDto(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(BindException ex) {
        logException(ex, LogLevel.INFO, false);

        return new ErrorDto(HttpStatus.BAD_REQUEST,
                extractMessage(ex.getBindingResult()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(MissingServletRequestParameterException ex) {
        logException(ex, LogLevel.INFO, false);

        return new ErrorDto(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(HttpMessageNotReadableException ex) {
        logException(ex, LogLevel.INFO, false);

        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException cause = (JsonMappingException) ex.getCause();
            return new ErrorDto(HttpStatus.BAD_REQUEST, cause);
        }

        return new ErrorDto(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto process(IllegalArgumentException ex) {
        logException(ex, LogLevel.INFO, false);

        return new ErrorDto(HttpStatus.BAD_REQUEST, ex);
    }

    private String extractMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(". "));
    }

    protected static void logException(Exception e, LogLevel level, boolean shouldLogStackTrace) {
        if (level == LogLevel.ERROR) {
            if (shouldLogStackTrace) {
                LOG.error("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            } else {
                LOG.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        } else if (level == LogLevel.WARN) {
            if (shouldLogStackTrace) {
                LOG.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            } else {
                LOG.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        } else if (level == LogLevel.INFO) {
            if (shouldLogStackTrace) {
                LOG.info("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            } else {
                LOG.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
