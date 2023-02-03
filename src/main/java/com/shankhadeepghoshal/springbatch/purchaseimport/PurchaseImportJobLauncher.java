package com.shankhadeepghoshal.springbatch.purchaseimport;

import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
public class PurchaseImportJobLauncher {
  JobLauncher launcher;
  @PurchaseImport Job purchaseImportJob;

  @SneakyThrows
  @Scheduled(fixedRate = 6L, timeUnit = TimeUnit.DAYS)
  public void performJob() {
    final var jobParameters =
        new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();

    log.info("Executing Purchase Job....");
    launcher.run(purchaseImportJob, jobParameters);
  }
}
