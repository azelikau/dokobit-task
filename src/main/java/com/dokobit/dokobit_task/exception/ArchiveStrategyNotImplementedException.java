package com.dokobit.dokobit_task.exception;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ArchiveStrategyNotImplementedException extends RuntimeException {

  private final ArchiveType archiveType;

}
