package com.dokobit.dokobit_task.controller.advice;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import com.dokobit.dokobit_task.exception.ArchiveStrategyNotImplementedException;
import com.dokobit.dokobit_task.exception.ReadMultipartRequestException;
import com.dokobit.dokobit_task.exception.WriteArchiveException;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler({IOException.class, FileUploadException.class})
  public ResponseEntity<ExceptionResponse> handleFileExceptions(Exception e) {

    log.error("The following exception occurred during : {} with root cause: {}",
        e.getMessage(), NestedExceptionUtils.getRootCause(e));

    return generateExceptionResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Unable to process file"
    );
  }

  @ExceptionHandler(ReadMultipartRequestException.class)
  public ResponseEntity<ExceptionResponse> handleReadMultipartException(
      ReadMultipartRequestException e) {

    log.error("Unable to read multipart request: {} with root cause : {}",
        e.getMessage(), getRootCauseMessage(e));

    return generateExceptionResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Unable to read multipart request"
    );
  }

  @ExceptionHandler(WriteArchiveException.class)
  public void handleWriteArchiveException(WriteArchiveException e) {

    log.error("Unable to write archive: {} with root cause : {}",
        e.getMessage(), getRootCauseMessage(e));
  }

  @ExceptionHandler(ArchiveStrategyNotImplementedException.class)
  public ResponseEntity<ExceptionResponse> handleArchiveStrategyNotImplemented(
      ArchiveStrategyNotImplementedException e) {

    log.debug("The following archive strategy is not implemented yet {}", e.getArchiveType());

    return generateExceptionResponse(
        HttpStatus.NOT_IMPLEMENTED,
        format("Archive type %s not supported", e.getArchiveType())
    );
  }

  @NonNull
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      @NonNull Exception ex, Object body,
      @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {

    String message = status.is5xxServerError() ?
        "An internal server error occurred" :
        ex.getMessage();

    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    return new ResponseEntity<>(new ExceptionResponse(message), headers, status);
  }

  private String getRootCauseMessage(Exception e) {
    Throwable rootCause = NestedExceptionUtils.getRootCause(e);

    if (isNull(rootCause)) {
      return "";
    }

    return rootCause.getMessage();
  }

  private ResponseEntity<ExceptionResponse> generateExceptionResponse(
      HttpStatus status,
      String message) {

    return ResponseEntity
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ExceptionResponse(message));
  }

  @AllArgsConstructor
  @Getter
  public static final class ExceptionResponse {

    private String message;
  }

}
