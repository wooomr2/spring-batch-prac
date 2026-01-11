package com.springbatch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class StepDurationTrackerListner implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("[StepDurationTrackerListner.beforeStep] Step 시작={} (Job={}, 시작 시각={})",
                stepExecution.getStepName(),
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getStartTime());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final LocalDateTime startTime = stepExecution.getStartTime();
        final LocalDateTime endTime = stepExecution.getEndTime();

        final long durationMillis = Duration.between(startTime, endTime).toMillis();

        long hours = durationMillis / (1000 * 60 * 60);
        long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (durationMillis % (1000 * 60)) / 1000;

        String duration;
        if (hours > 0) {
            duration = String.format("%d시간 %d분 %d초", hours, minutes, seconds);
        } else if (minutes > 0) {
            duration = String.format("%d분 %d초", minutes, seconds);
        } else {
            duration = String.format("%d초", seconds);
        }

        log.info("[StepDurationTrackerListner.afterStep] Step 종료={}, 상태={}, 읽음={}건, 기록={}건, 프로세스skip={} 스킵={}건, 총 소요시간={})",
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getProcessSkipCount(),
                stepExecution.getSkipCount(),
                duration
        );

        return stepExecution.getExitStatus();
    }
}
