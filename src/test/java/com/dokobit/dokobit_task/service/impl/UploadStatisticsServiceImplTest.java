package com.dokobit.dokobit_task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dokobit.dokobit_task.dao.entity.UploadStatistics;
import com.dokobit.dokobit_task.dao.entity.UploadStatistics.UploadStatisticsId;
import com.dokobit.dokobit_task.dao.repository.UploadStatisticsRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadStatisticsServiceImplTest {

  @Mock
  private UploadStatisticsRepository uploadStatisticsRepository;

  @InjectMocks
  private UploadStatisticsServiceImpl uploadStatisticsService;

  @Test
  void testRecordSingleUpload_uploadStatisticsForIpAlreadyExists() {
    String clientIp = "client ip";

    long usageCount = 5;

    UploadStatisticsId id =
        UploadStatisticsId.builder()
            .clientIp(clientIp)
            .date(LocalDate.now())
            .build();

    when(uploadStatisticsRepository.findById(id))
        .thenReturn(Optional.of(
            UploadStatistics.builder()
                .id(id)
                .usageCount(usageCount)
                .build()));

    uploadStatisticsService.recordSingleUpload(clientIp);

    ArgumentCaptor<UploadStatistics> uploadStatisticsCaptor =
        ArgumentCaptor.forClass(UploadStatistics.class);

    verify(uploadStatisticsRepository).save(uploadStatisticsCaptor.capture());

    UploadStatistics savedStatistics = uploadStatisticsCaptor.getValue();

    assertThat(savedStatistics.getId()).isEqualTo(id);
    assertThat(savedStatistics.getUsageCount()).isEqualTo(++usageCount);
  }

  @Test
  void testRecordSingleUpload_uploadStatisticsForIpNotExists() {
    String clientIp = "client ip";

    UploadStatisticsId id =
        UploadStatisticsId.builder()
            .clientIp(clientIp)
            .date(LocalDate.now())
            .build();

    when(uploadStatisticsRepository.findById(id)).thenReturn(Optional.empty());

    uploadStatisticsService.recordSingleUpload(clientIp);

    ArgumentCaptor<UploadStatistics> uploadStatisticsCaptor =
        ArgumentCaptor.forClass(UploadStatistics.class);

    verify(uploadStatisticsRepository).save(uploadStatisticsCaptor.capture());

    UploadStatistics savedStatistics = uploadStatisticsCaptor.getValue();

    assertThat(savedStatistics.getId()).isEqualTo(id);
    assertThat(savedStatistics.getUsageCount()).isEqualTo(1);
  }


}