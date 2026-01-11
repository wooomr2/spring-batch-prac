package com.springbatch.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class SampleChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("sample 1: [SampleItemWriterListener.beforeChunk] 청크 시작");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("sample 1: [SampleItemWriterListener.afterChunk] 청크 종료");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.info("sample 1: [SampleItemWriterListener.afterChunkError] 청크 에러 발생");
    }
}
