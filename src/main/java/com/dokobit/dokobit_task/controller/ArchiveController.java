package com.dokobit.dokobit_task.controller;

import static java.lang.String.format;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.exception.ReadMultipartRequestException;
import com.dokobit.dokobit_task.model.ArchiveDTO;
import com.dokobit.dokobit_task.service.ArchiveService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
@RequestMapping("/archive")
@RequiredArgsConstructor
@Slf4j
public class ArchiveController {

  private final ArchiveService archiveService;
  private final ServletFileUpload fileUpload;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<StreamingResponseBody> createArchive(
      HttpServletRequest request,
      @RequestParam("archive_type") ArchiveType archiveType)
      throws HttpMediaTypeNotSupportedException {

    if (!ServletFileUpload.isMultipartContent(request)) {
      throw new HttpMediaTypeNotSupportedException("Content type is invalid");
    }

    log.debug("Received create {} archive request from client {}",
        archiveType, request.getRemoteAddr());

    FileItemIterator fileItemIterator;

    try {
      fileItemIterator = fileUpload.getItemIterator(request);
    } catch (FileUploadException | IOException e) {
      throw new ReadMultipartRequestException(e);
    }

    ArchiveDTO archiveDTO =
        archiveService.createArchive(request.getRemoteAddr(), archiveType, fileItemIterator);

    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(archiveType.getMediaTypeString()))
        .cacheControl(CacheControl.noCache())
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            format("attachment; filename=%s", archiveDTO.getFileNameWithExtension()))
        .body(archiveDTO.getResponseConsumer()::accept);
  }

}
