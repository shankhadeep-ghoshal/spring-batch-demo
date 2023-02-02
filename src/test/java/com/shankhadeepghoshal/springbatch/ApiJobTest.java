package com.shankhadeepghoshal.springbatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@SpringBootTest
@SpringBatchTest
class ApiJobTest {

    @Autowired private transient JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void testJob() throws Exception {
        final var jobExecution = jobLauncherTestUtils.launchJob();
        final var stepExecution = jobExecution.getStepExecutions().iterator().next();

        Assertions.assertAll(
                () -> Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus()),
                () -> Assertions.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus()),
                () -> Assertions.assertEquals(10, stepExecution.getReadCount()),
                () -> Assertions.assertEquals(0, stepExecution.getWriteCount()));
    }
}
