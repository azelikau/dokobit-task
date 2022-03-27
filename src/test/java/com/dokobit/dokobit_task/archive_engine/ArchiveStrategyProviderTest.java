package com.dokobit.dokobit_task.archive_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.dokobit.dokobit_task.enumeration.ArchiveType;
import com.dokobit.dokobit_task.exception.ArchiveStrategyNotImplementedException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArchiveStrategyProviderTest {

  @Test
  void testGetStrategy_noStrategyFound() {
    ArchiveStrategyProvider archiveStrategyProvider =
        new ArchiveStrategyProvider(Collections.emptyList());

    assertThrows(
        ArchiveStrategyNotImplementedException.class,
        () -> archiveStrategyProvider.getStrategy(ArchiveType.ZIP));
  }

  @Test
  void testGetStrategy() {
    ArchiveType archiveType = ArchiveType.ZIP;

    ArchiveStrategy archiveStrategy = Mockito.mock(ArchiveStrategy.class);

    when(archiveStrategy.getType()).thenReturn(archiveType);

    ArchiveStrategyProvider archiveStrategyProvider =
        new ArchiveStrategyProvider(Collections.singletonList(archiveStrategy));

    ArchiveStrategy actual = archiveStrategyProvider.getStrategy(archiveType);

    assertEquals(actual, archiveStrategy);
  }
}