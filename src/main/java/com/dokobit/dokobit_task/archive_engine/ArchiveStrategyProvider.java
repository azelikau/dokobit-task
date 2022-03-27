package com.dokobit.dokobit_task.archive_engine;

import static java.util.Objects.isNull;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.exception.ArchiveStrategyNotImplementedException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ArchiveStrategyProvider {

  private final Map<ArchiveType, ArchiveStrategy> strategiesMap;

  public ArchiveStrategyProvider(List<ArchiveStrategy> strategies) {
    strategiesMap = strategies.stream()
        .collect(Collectors.toMap(ArchiveStrategy::getType, Function.identity()));
  }

  public ArchiveStrategy getStrategy(ArchiveType archiveType) {
    ArchiveStrategy strategy = strategiesMap.get(archiveType);

    if(isNull(strategy)) {
      throw new ArchiveStrategyNotImplementedException(archiveType);
    }

    return strategy;
  }

}
