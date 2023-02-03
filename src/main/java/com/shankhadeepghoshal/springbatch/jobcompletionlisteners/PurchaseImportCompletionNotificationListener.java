package com.shankhadeepghoshal.springbatch.jobcompletionlisteners;

import com.shankhadeepghoshal.springbatch.annotations.PurchaseImport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
@PurchaseImport
public class PurchaseImportCompletionNotificationListener implements JobExecutionListener {
    public static final String SELECT_QUERY =
            """
											SELECT COUNT(*) FROM person_purchase
							""";
    JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (BatchStatus.COMPLETED == jobExecution.getStatus()) {
            log.info("Job Completed. To Verify");

            jdbcTemplate
                    .query(SELECT_QUERY, (rs, row) -> rs.getInt(1))
                    .forEach(
                            count -> log.info("currently {} rows are present in the table", count));
        }
    }
}
