package com.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class PaymentReportJobConfig {

    // Job name + Job Parameter가 unique한지 감지 함
    @Bean
    public Job paymentReportJob(JobRepository jobRepository,
                                Step paymentReportStep) {

        return new JobBuilder("paymentReportJob", jobRepository)
                .start(paymentReportStep)
                .build();
    }
    
    @Bean
    public Step paymentReportStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("paymentReportStep", jobRepository)
                .<BigDecimal, BigDecimal>chunk(5, transactionManager)
                .reader(paymentItemReader())
                .writer(paymentItemWriter())
                .build();
    }

    private ItemReader<BigDecimal> paymentItemReader() {
        return new ListItemReader<>(getPayments());
    }

    private ItemWriter<BigDecimal> paymentItemWriter() {
        return items -> items.forEach(System.out::println);
    }

    private List<BigDecimal> getPayments() {
        return List.of(
                BigDecimal.valueOf(100.50),
                BigDecimal.valueOf(200.75),
                BigDecimal.valueOf(300.00),
                BigDecimal.valueOf(400.25),
                BigDecimal.valueOf(500.80),
                BigDecimal.valueOf(600.10),
                BigDecimal.valueOf(100.50),
                BigDecimal.valueOf(200.75),
                BigDecimal.valueOf(300.00),
                BigDecimal.valueOf(400.25),
                BigDecimal.valueOf(500.80),
                BigDecimal.valueOf(600.10),
                BigDecimal.valueOf(200.75),
                BigDecimal.valueOf(300.00),
                BigDecimal.valueOf(400.25),
                BigDecimal.valueOf(500.80),
                BigDecimal.valueOf(600.10),
                BigDecimal.valueOf(200.75),
                BigDecimal.valueOf(300.00),
                BigDecimal.valueOf(400.25),
                BigDecimal.valueOf(500.80),
                BigDecimal.valueOf(600.10),
                BigDecimal.valueOf(200.75),
                BigDecimal.valueOf(300.00),
                BigDecimal.valueOf(400.25),
                BigDecimal.valueOf(500.80),
                BigDecimal.valueOf(600.10)
        );
    }
}
