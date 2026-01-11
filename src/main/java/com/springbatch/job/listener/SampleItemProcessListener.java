package com.springbatch.job.listener;

import com.springbatch.entity.Payment;
import com.springbatch.entity.PaymentSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class SampleItemProcessListener implements ItemProcessListener<PaymentSource, Payment> {

    @Override
    public void beforeProcess(PaymentSource item) {
        log.info("sample 3: [SampleItemProcesListener.beforeProcess] 아이템 처리 시작, item={}", item);
    }

    @Override
    public void afterProcess(PaymentSource item, Payment result) {
        log.info("sample 3: [SampleItemProcesListener.afterProcess] 아이템 처리 종료, item={}, result={}", item, result);
    }

    @Override
    public void onProcessError(PaymentSource item, Exception e) {
        log.info("sample 3: [SampleItemProcesListener.onProcessError] 아이템 처리 에러 발생, item={}, exception={}",
                item, e.getMessage());
    }
}
