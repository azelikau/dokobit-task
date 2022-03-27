package com.dokobit.dokobit_task.dao.entity;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadStatistics {

  @EmbeddedId
  private UploadStatisticsId id;
  private Long usageCount;

  public void incrementUsageCount() {
    if (isNull(usageCount)) {
      usageCount = 0L;
    } else {
      usageCount += 1;
    }
  }

  @Embeddable
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UploadStatisticsId implements Serializable {

    private String clientIp;
    private LocalDate date;

  }

}
