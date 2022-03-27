package com.dokobit.dokobit_task.service.impl;

import com.dokobit.dokobit_task.dao.entity.UploadStatistics;
import com.dokobit.dokobit_task.dao.entity.UploadStatistics.UploadStatisticsId;
import com.dokobit.dokobit_task.dao.repository.UploadStatisticsRepository;
import com.dokobit.dokobit_task.service.UploadStatisticsService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UploadStatisticsServiceImpl implements UploadStatisticsService {

  private final UploadStatisticsRepository repository;

  @Async
  @Override
  public void recordSingleUpload(String clientIp) {
    log.debug("Storing the upload statistics record for client {}", clientIp);

    UploadStatisticsId id =
        UploadStatisticsId.builder()
            .clientIp(clientIp)
            .date(LocalDate.now())
            .build();

    UploadStatistics statistics =
        repository.findById(id)
            .orElse(UploadStatistics.builder()
                .id(id)
                .usageCount(0L)
                .build());

    statistics.incrementUsageCount();

    repository.save(statistics);
  }
}
