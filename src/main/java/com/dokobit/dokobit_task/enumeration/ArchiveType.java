package com.dokobit.dokobit_task.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ArchiveType {
  ZIP("application/zip", ".zip");

  private final String mediaTypeString;
  private final String fileExtension;
}
