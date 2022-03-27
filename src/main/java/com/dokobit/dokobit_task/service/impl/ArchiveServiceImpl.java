package com.dokobit.dokobit_task.service.impl;

import com.dokobit.dokobit_task.archive_engine.ArchiveStrategy;
import com.dokobit.dokobit_task.archive_engine.ArchiveStrategyProvider;
import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.model.ArchiveDTO;
import com.dokobit.dokobit_task.service.ArchiveService;
import com.dokobit.dokobit_task.service.UploadStatisticsService;
import java.io.OutputStream;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItemIterator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveServiceImpl implements ArchiveService {

  private final ArchiveStrategyProvider archiveStrategyProvider;
  private final UploadStatisticsService uploadStatisticsService;

  @Override
  public ArchiveDTO createArchive(
      String clientIp,
      ArchiveType archiveType,
      FileItemIterator fileItemIterator) {

    ArchiveStrategy strategy = archiveStrategyProvider.getStrategy(archiveType);

    Consumer<OutputStream> contentWriter = out -> strategy.writeArchive(fileItemIterator, out);

    Consumer<OutputStream> responseConsumer =
        contentWriter.andThen(out -> uploadStatisticsService.recordSingleUpload(clientIp));

    return ArchiveDTO.builder()
        .filename(generateFilename())
        .extension(archiveType.getFileExtension())
        .responseConsumer(responseConsumer)
        .build();
  }

  private String generateFilename() {
    return "result";
  }
}
