package com.dokobit.dokobit_task.service;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.model.ArchiveDTO;
import org.apache.commons.fileupload.FileItemIterator;


public interface ArchiveService {

  ArchiveDTO createArchive(
      String clientIp,
      ArchiveType archiveType,
      FileItemIterator files);

}
