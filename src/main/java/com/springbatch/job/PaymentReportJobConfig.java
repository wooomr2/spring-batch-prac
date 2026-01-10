package com.springbatch.job;

import com.springbatch.entity.Payment;
import com.springbatch.entity.PaymentSource;
import com.springbatch.exception.InvalidPaymentAmountException;
import com.springbatch.repository.PaymentRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Configuration
@AllArgsConstructor
public class PaymentReportJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    private final PaymentRepository paymentRepository;

    @Bean
    public Job paymentReportJob(Step paymentReportStep) {
        return new JobBuilder("paymentReportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(paymentReportStep)
                .build();
    }

    @Bean
    public Step paymentReportStep(
            JpaPagingItemReader<PaymentSource> paymentReportReader
    ) {
        return new StepBuilder("paymentReportStep", jobRepository)
                .<PaymentSource, Payment>chunk(10, transactionManager)
                .reader(paymentReportReader)
                .processor(itemProcessor())
//                .writer(itemWriter())
//                .writer(paymentItemWriter())
                .writer(paymentReportWriter())
                .faultTolerant()
//                .skipLimit(1) // 최대 n번까지 skip 허용
//                .skip(InvalidPaymentAmountException.class)
//                .skipPolicy(new LimitCheckingItemSkipPolicy())
                .skipPolicy(new LimitCheckingItemSkipPolicy(2, throwable -> {
                    if (throwable instanceof InvalidPaymentAmountException) {
                        return true;
                    } else if (throwable instanceof IllegalArgumentException) {
                        return false;
                    } else {
                        return false;
                    }
                }))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<PaymentSource> paymentReportReader(
            @Value("#{jobParameters['paymentDate']}") LocalDate paymentDate
    ) {
        return new JpaPagingItemReaderBuilder<PaymentSource>()
                .name("paymentReportReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                        SELECT ps
                        FROM PaymentSource ps
                        WHERE ps.paymentDate = :paymentDate
                        """)
                .parameterValues(Collections.singletonMap("paymentDate", paymentDate))
                .pageSize(10)
                .build();
    }

    private ItemProcessor<PaymentSource, Payment> itemProcessor() {
        return paymentSource -> {
//            /* 최종금액이 0원인 경우 제외 */
//            if (paymentSource.getFinalAmount().compareTo(BigDecimal.ZERO) == 0) {
//                return null;
//            }

            /* 할인금액이 마이너스가 되는 케이스(원장에서 처리가 잘못된 case) */
            if (paymentSource.getDiscountAmount().signum() == -1) {
                final String msg = "할인금액이 0이 아닌 결제는 처리할 수 없습니다. 현재 할인금액" + paymentSource.getDiscountAmount();
                log.error(msg);
                throw new InvalidPaymentAmountException(msg);
            }

            return new Payment(null,
                    paymentSource.getFinalAmount(),
                    paymentSource.getPaymentDate(),
                    "COMPLETED"
            );
        };
    }

//    private ItemWriter<Payment> itemWriter() {
//        return paymentRepository::saveAllAndFlush;
//    }

//    @Bean
//    public JpaItemWriter<Payment> paymentItemWriter() {
//        JpaItemWriter<Payment> writer = new JpaItemWriter<>();
//        writer.setEntityManagerFactory(entityManagerFactory);
//        return writer;
//    }

    @Bean
    public ItemWriter<Payment> paymentReportWriter() {
        return chunk -> {
            for (Payment payment : chunk) {
                log.info("Writer paymennt: {}", payment);
            }
        };
    }
}
