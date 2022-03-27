package com.dokobit.dokobit_task.model;

import java.io.OutputStream;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArchiveDTO {

  private String filename;
  private String extension;
  private Consumer<OutputStream> responseConsumer;

  public String getFileNameWithExtension() {
    return filename.concat(extension);
  }

}
