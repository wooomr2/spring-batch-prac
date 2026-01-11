package com.springbatch.job.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class JobDurationTrackerListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[JobDurationTrackerListener.beforeJob] Job 시작={} (시작 시각={})",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        final LocalDateTime startTime = jobExecution.getStartTime();
        final LocalDateTime endTime = jobExecution.getEndTime();

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

        log.info("[JobDurationTrackerListener.afterJob] Job 종료={}, 총 소요시간={} (종료 시각={})",
                jobExecution.getJobInstance().getJobName(),
                duration,
                jobExecution.getEndTime());

        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("[JobDurationTrackerListener.afterJob] Job 실패 사유={}",
                    jobExecution.getAllFailureExceptions());
        }
    }
}
