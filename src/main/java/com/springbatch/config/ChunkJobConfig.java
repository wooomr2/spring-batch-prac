package com.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.LongStream;

@Configuration
public class ChunkJobConfig {

    @Bean
    public Job chunkJob(
            JobRepository jobRepository,
            Step chunkStep
    ) {
        return new JobBuilder("chunkJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkStep)
                .build();
    }

    @Bean
    public Step chunkStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("chunkStep", jobRepository)
                .<Long, Long>chunk(10, transactionManager)
                .reader(chunkItemReader())
                .processor(chunkItemProcessor())
                .writer(chunkItemWriter())
                .build();
    }

    private ItemReader<Long> chunkItemReader() {
        return new ListItemReader<>(getItems());
    }

    private ItemProcessor<Long, Long> chunkItemProcessor() {
        return item -> {
            if (item % 2 == 0) {
                return item;
            } else {
                return null; // Filter out odd numbers
            }
        };
    }

    private ItemWriter<Long> chunkItemWriter() {
        return items -> items.forEach(System.out::println); // Implement your ItemWriter here
    }

    private List<Long> getItems() {
        return LongStream.rangeClosed(0, 100).boxed().toList();
    }
}
