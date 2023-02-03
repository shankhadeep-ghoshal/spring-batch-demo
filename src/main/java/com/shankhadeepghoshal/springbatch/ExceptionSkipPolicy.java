package com.shankhadeepghoshal.springbatch;

import com.shankhadeepghoshal.springbatch.personimport.PersonImportException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@Component
public class ExceptionSkipPolicy implements SkipPolicy {

  @Override
  public boolean shouldSkip(Throwable throwable, long skipCount)
      throws SkipLimitExceededException { // NOPMD
    log.error("Error occurred while job execution", throwable);

    return throwable instanceof IllegalArgumentException
        || throwable instanceof PersonImportException;
  }
}
