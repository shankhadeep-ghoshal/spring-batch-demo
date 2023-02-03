package com.shankhadeepghoshal.springbatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@SpringBootTest
@SpringBatchTest
@AutoConfigureTestDatabase
@DirtiesContext
class ApiStepTest {
  @Autowired private transient JobLauncherTestUtils jobLauncherTestUtils;

  @Value("${job.person.step.first}")
  private transient String stepName;

  @Test
  void testStep() {
    final var jobExecution = jobLauncherTestUtils.launchStep(stepName);
    final var stepExecution = jobExecution.getStepExecutions().iterator().next();

    Assertions.assertAll(
        () -> Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus()),
        () -> Assertions.assertEquals(10, stepExecution.getReadCount()),
        () -> Assertions.assertEquals(10, stepExecution.getWriteCount()));
  }
}
