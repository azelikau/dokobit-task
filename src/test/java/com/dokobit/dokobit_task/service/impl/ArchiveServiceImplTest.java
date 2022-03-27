package com.dokobit.dokobit_task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dokobit.dokobit_task.archive_engine.ArchiveStrategy;
import com.dokobit.dokobit_task.archive_engine.ArchiveStrategyProvider;
import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.model.ArchiveDTO;
import com.dokobit.dokobit_task.service.UploadStatisticsService;
import java.io.OutputStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArchiveServiceImplTest {

  @Mock
  private UploadStatisticsService uploadStatisticsService;

  @Mock
  private ArchiveStrategyProvider archiveStrategyProvider;

  @InjectMocks
  private ArchiveServiceImpl archiveService;

  @Test
  void testCreateArchive() {
    String clientIp = "client ip";
    ArchiveType archiveType = ArchiveType.ZIP;
    FileItemIterator fileItemIterator = Mockito.mock(FileItemIterator.class);

    ArchiveStrategy archiveStrategy = Mockito.mock(ArchiveStrategy.class);

    when(archiveStrategyProvider.getStrategy(archiveType))
        .thenReturn(archiveStrategy);

    ArchiveDTO archiveDTO = archiveService.createArchive(clientIp, archiveType, fileItemIterator);

    assertThat(archiveDTO.getFilename()).isNotBlank();
    assertThat(archiveDTO.getExtension()).isEqualTo(archiveDTO.getExtension());

    OutputStream outputStream = Mockito.mock(OutputStream.class);

    archiveDTO.getResponseConsumer().accept(outputStream);

    verify(archiveStrategy).writeArchive(fileItemIterator, outputStream);
    verify(uploadStatisticsService).recordSingleUpload(clientIp);
  }

}