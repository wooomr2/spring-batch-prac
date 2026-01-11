package com.springbatch.job;

import com.springbatch.entity.Payment;
import com.springbatch.entity.PaymentSource;
import com.springbatch.job.sample.SampleChunkListener;
import com.springbatch.job.sample.SampleItemProcessListener;
import com.springbatch.job.sample.SampleItemReadListener;
import com.springbatch.job.sample.SampleItemWriterListener;
import com.springbatch.repository.PaymentRepository;
import com.springbatch.service.PartnerCorpService;
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
    private final PartnerCorpService partnerCorpService;

    @Bean
    public Job paymentReportJob(Step paymentReportStep) {
        return new JobBuilder("paymentReportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobDurationTrackerListener())
                .start(paymentReportStep)
                .build();
    }

    @Bean
    public Step paymentReportStep(
            JpaPagingItemReader<PaymentSource> paymentReportReader
    ) {

        /**
         * 1. 상호명을 더이상 PaymentSource에서 관리하지 않음
         * 2. PaymentSource에서는 사업자번호를 추가하고 이 사업자번호 기반으로 
         * 3. Payment를 저장할 때 사업자번호를 기준으로 HTTP 통신하여 상호명 질의
         * */
        return new StepBuilder("paymentReportStep", jobRepository)
                .<PaymentSource, Payment>chunk(10, transactionManager)
                .listener(new StepDurationTrackerListner())
                .reader(paymentReportReader)
                .processor(itemProcessor())
                .writer(paymentReportWriter())
                /**
                 * 1. chunk: SampleChunkListener
                 * 2. reader: SamepleItemReadListener
                 * 3. processor: SampleItemProcessListesner
                 * 4. writer: SampleItemWriterListesner
                 * */
                .listener(new SampleChunkListener())
                .listener(new SampleItemReadListener())
                .listener(new SampleItemProcessListener())
                .listener(new SampleItemWriterListener())
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

//            String partnerCorpName = partnerCorpService.getPartnerCorpName(paymentSource.getPartnerBusinessRegistrationNumber());
            String partnerCorpName = "tmpParterCropName";

            return new Payment(null,
                    paymentSource.getFinalAmount(),
                    paymentSource.getPaymentDate(),
                    partnerCorpName,
                    "COMPLETED"
            );
        };
    }

    @Bean
    public ItemWriter<Payment> paymentReportWriter() {
        return chunk -> {
            for (Payment payment : chunk) {
                log.info("Writer payment: {}", payment);
            }
        };
    }
}
