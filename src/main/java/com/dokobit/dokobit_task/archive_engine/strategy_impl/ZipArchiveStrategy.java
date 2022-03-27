package com.dokobit.dokobit_task.archive_engine.strategy_impl;

import com.dokobit.dokobit_task.archive_engine.ArchiveStrategy;
import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.exception.WriteArchiveException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZipArchiveStrategy implements ArchiveStrategy {

  @Override
  public void writeArchive(FileItemIterator fileItemIterator, OutputStream out) {

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {

      while (fileItemIterator.hasNext()) {
        writeSingleFile(fileItemIterator.next(), zipOutputStream);
      }

    } catch (IOException | FileUploadException e) {
      throw new WriteArchiveException(e);
    }
  }

  private void writeSingleFile(
      FileItemStream fileItemStream,
      ZipOutputStream outputStream) throws IOException {

    log.debug("Start writing file {}", fileItemStream.getName());

    try (InputStream fileInputStream = fileItemStream.openStream()) {

      outputStream.putNextEntry(new ZipEntry(fileItemStream.getName()));

      IOUtils.copy(fileInputStream, outputStream);

      outputStream.closeEntry();
    }

    log.debug("Written file {}", fileItemStream.getName());
  }

  @Override
  public ArchiveType getType() {
    return ArchiveType.ZIP;
  }
}
