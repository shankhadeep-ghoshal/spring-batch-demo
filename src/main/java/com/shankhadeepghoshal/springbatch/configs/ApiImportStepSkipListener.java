package com.shankhadeepghoshal.springbatch.configs;

import com.shankhadeepghoshal.springbatch.annotations.ApiImport;
import com.shankhadeepghoshal.springbatch.pojos.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@Component
@ApiImport
public class ApiImportStepSkipListener implements SkipListener<Person, Number> {

    @Override
    public void onSkipInRead(Throwable throwable) {
        log.info("Fail during read", throwable);
    }

    @Override
    public void onSkipInWrite(Number item, Throwable throwable) {
        log.info("Fail during write at {}", item, throwable);
    }

    @Override
    public void onSkipInProcess(Person item, Throwable throwable) {
        log.info("Item {} skipped due to exception {}", item, throwable.getMessage(), throwable);
    }
}
