package com.dokobit.dokobit_task.controller;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.model.ArchiveDTO;
import com.dokobit.dokobit_task.service.ArchiveService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(ArchiveController.class)
class ArchiveControllerTest {

  private static final String URL = "/archive";
  private static final String ARCHIVE_TYPE_QUERY_PARAM = "archive_type";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ArchiveService archiveService;

  @SpyBean
  private ServletFileUpload servletFileUpload;

  @Test
  @SneakyThrows
  void testCreateArchive_invalidContentType() {
    mockMvc.perform(post(URL)
            .queryParam(ARCHIVE_TYPE_QUERY_PARAM, ArchiveType.ZIP.name())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnsupportedMediaType());

    verify(servletFileUpload, never()).getItemIterator(any(HttpServletRequest.class));
    verify(archiveService, never()).createArchive(any(), any(), any());
  }

  @Test
  @SneakyThrows
  void testCreateArchive_unableToReadMultipartRequest() {
    doThrow(new IOException()).when(servletFileUpload)
        .getItemIterator(any(HttpServletRequest.class));

    mockMvc.perform(post(URL)
            .queryParam(ARCHIVE_TYPE_QUERY_PARAM, ArchiveType.ZIP.name())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @SneakyThrows
  void testCreateArchive() {
    ArchiveType archiveType = ArchiveType.ZIP;

    String clientIP = "some ip address";

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "file.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello world!".getBytes(StandardCharsets.UTF_8)
    );

    ArchiveDTO archiveDTO = ArchiveDTO.builder()
        .filename("result")
        .extension(archiveType.getFileExtension())
        .responseConsumer(out -> {
          try {
            out.write(file.getBytes());
          } catch (IOException ignored) {
          }
        })
        .build();

    when(archiveService.createArchive(eq(clientIP), eq(archiveType), any())).thenReturn(archiveDTO);

    MockHttpServletRequestBuilder requestBuilder =
        post(URL)
            .queryParam(ARCHIVE_TYPE_QUERY_PARAM, archiveType.name())
            .with(request -> {
              request.setRemoteAddr(clientIP);
              return request;
            });

    mockMvc.perform(buildMultipartRequest(requestBuilder, file))
        .andExpect(status().isOk())
        .andExpect(content().contentType(archiveType.getMediaTypeString()))
        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
            format("attachment; filename=%s", archiveDTO.getFileNameWithExtension())))
        .andExpect(content().bytes(file.getBytes()));

    ArgumentCaptor<FileItemIterator> fileItemIteratorCaptor =
        ArgumentCaptor.forClass(FileItemIterator.class);

    verify(archiveService).createArchive(
        eq(clientIP),
        eq(archiveType),
        fileItemIteratorCaptor.capture());

    FileItemIterator fileItemIterator = fileItemIteratorCaptor.getValue();

    assertThat(fileItemIterator.hasNext()).isTrue();

    String content = new BufferedReader(
        new InputStreamReader(fileItemIterator.next().openStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    assertThat(content).isEqualTo(new String(file.getBytes()));
  }

  private MockHttpServletRequestBuilder buildMultipartRequest(
      MockHttpServletRequestBuilder builder,
      MockMultipartFile file) {

    String boundary = "q1w2e3r4t5y6u7i8o9";

    return builder
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE + "; boundary=" + boundary)
        .content(generateMultipartRequestBody(file, boundary));
  }

  @SneakyThrows
  private byte[] generateMultipartRequestBody(MockMultipartFile file, String boundary) {
    String start = format(
        "--%s\r\n"
            + "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n"
            + "Content-Type: %s\r\n\r\n",
        boundary, file.getName(), file.getOriginalFilename(), file.getContentType());

    String content = new String(file.getBytes());

    String end = format("\r\n--%s--", boundary);

    return (start + content + end).getBytes(StandardCharsets.UTF_8);
  }


}