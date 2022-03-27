package com.dokobit.dokobit_task.dao.repository;

import com.dokobit.dokobit_task.dao.entity.UploadStatistics;
import com.dokobit.dokobit_task.dao.entity.UploadStatistics.UploadStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadStatisticsRepository extends
    JpaRepository<UploadStatistics, UploadStatisticsId> {

}
