package com.dokobit.dokobit_task.archive_engine.strategy_impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.dokobit.dokobit_task.exception.WriteArchiveException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.SneakyThrows;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ZipArchiveStrategyTest {

  private final ZipArchiveStrategy zipArchiveStrategy = new ZipArchiveStrategy();

  @Test
  @SneakyThrows
  void testWriteArchive() {
    String fileName = "file.txt";
    String fileContent = "Hello world!";

    FileItemIterator fileItemIterator = Mockito.mock(FileItemIterator.class);

    FileItemStream fileItemStream = Mockito.mock(FileItemStream.class);

    when(fileItemIterator.hasNext()).thenReturn(true, false);
    when(fileItemIterator.next()).thenReturn(fileItemStream);

    when(fileItemStream.getName()).thenReturn(fileName);
    when(fileItemStream.openStream())
        .thenReturn(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

    PipedOutputStream outputStream = new PipedOutputStream();
    PipedInputStream inputStream = new PipedInputStream(outputStream);

    zipArchiveStrategy.writeArchive(fileItemIterator, outputStream);

    ZipInputStream zipInputStream = new ZipInputStream(inputStream);

    ZipEntry zipEntry = zipInputStream.getNextEntry();

    assertThat(zipEntry).isNotNull();
    assertThat(zipEntry.getName()).isEqualTo(fileName);

    String unzippedContent = new BufferedReader(new InputStreamReader(zipInputStream)).lines()
        .collect(Collectors.joining("\n"));

    assertThat(unzippedContent).isEqualTo(fileContent);
  }

  @Test
  @SneakyThrows
  void testWriteArchive_exceptionOccurred() {
    FileItemIterator fileItemIterator = Mockito.mock(FileItemIterator.class);

    FileItemStream fileItemStream = Mockito.mock(FileItemStream.class);

    when(fileItemIterator.hasNext()).thenReturn(true, false);
    when(fileItemIterator.next()).thenReturn(fileItemStream);

    doThrow(new IOException()).when(fileItemStream).openStream();

    assertThrows(
        WriteArchiveException.class,
        () -> zipArchiveStrategy.writeArchive(fileItemIterator, Mockito.mock(OutputStream.class)));
  }

}