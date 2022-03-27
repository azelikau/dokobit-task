package com.dokobit.dokobit_task.archive_engine;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.fileupload.FileItemIterator;
import org.springframework.web.multipart.MultipartFile;


public interface ArchiveStrategy {

  void writeArchive(FileItemIterator fileItemIterator, OutputStream out);

  ArchiveType getType();

}
