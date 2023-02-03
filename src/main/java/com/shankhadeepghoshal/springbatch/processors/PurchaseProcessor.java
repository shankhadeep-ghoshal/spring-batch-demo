package com.shankhadeepghoshal.springbatch.processors;

import com.shankhadeepghoshal.springbatch.annotations.PurchaseImport;
import com.shankhadeepghoshal.springbatch.pojos.Purchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Slf4j
@Component
@PurchaseImport
public class PurchaseProcessor implements ItemProcessor<Purchase, Purchase> {

    @Override
    public Purchase process(Purchase purchase) {
        log.info("Processing purchase {} ", purchase);
        return purchase;
    }
}
