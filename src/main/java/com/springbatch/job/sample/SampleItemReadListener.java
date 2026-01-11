package com.springbatch.job.sample;

import com.springbatch.entity.PaymentSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class SampleItemReadListener implements ItemReadListener<PaymentSource> {

    @Override
    public void beforeRead() {
        log.info("sample 2: [SampleItemReadListener.beforeRead] 아이템 읽기 시작");
    }

    @Override
    public void afterRead(PaymentSource item) {
        log.info("sample 2: [SampleItemReadListener.afterRead] 아이템 읽기 종료, item={}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.info("sample 2: [SampleItemReadListener.onReadError] 아이템 읽기 에러 발생, exception={}", ex.getMessage());
    }
}
