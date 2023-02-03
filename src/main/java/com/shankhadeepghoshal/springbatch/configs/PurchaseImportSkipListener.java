package com.shankhadeepghoshal.springbatch.configs;

import com.shankhadeepghoshal.springbatch.annotations.PurchaseImport;
import com.shankhadeepghoshal.springbatch.pojos.Purchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@Component
@PurchaseImport
public class PurchaseImportSkipListener implements SkipListener<Purchase, Number> {

    @Override
    public void onSkipInRead(Throwable throwable) {
        log.info("Fail during read Purchase", throwable);
    }

    @Override
    public void onSkipInWrite(Number item, Throwable throwable) {
        log.info("Fail during write at {} for Purchase", item, throwable);
    }

    @Override
    public void onSkipInProcess(Purchase item, Throwable throwable) {
        log.info(
                "Purchase {} skipped due to exception {}", item, throwable.getMessage(), throwable);
    }
}
