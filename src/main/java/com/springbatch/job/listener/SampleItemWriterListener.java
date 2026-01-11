package com.springbatch.job.listener;

import com.springbatch.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class SampleItemWriterListener implements ItemWriteListener<Payment> {

    @Override
    public void beforeWrite(Chunk<? extends Payment> items) {
        log.info("sample 4: [SampleItemWriterListener.beforeWrite] 아이템 쓰기 시작, items={}", items.getItems());
    }

    @Override
    public void afterWrite(Chunk<? extends Payment> items) {
        log.info("sample 4: [SampleItemWriterListener.afterWrite] 아이템 쓰기 종료, items={}", items.getItems());
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Payment> items) {
        log.info("sample 4: [SampleItemWriterListener.onWriteError] 아이템 쓰기 에러 발생, exception={}, items={}",
                exception.getMessage(), items.getItems());
    }
}
