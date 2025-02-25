package org.app.demokeyclock;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class DemoKeyclockApplication implements CommandLineRunner {

    private final JobLauncher jobLauncher;


    private final Job importClientJob;

    public static void main(String[] args) {
        SpringApplication.run(DemoKeyclockApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Use unique job parameters on each run
        JobExecution execution = jobLauncher.run(importClientJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters());
        log.info("Job Status : {}", execution.getStatus());
    }


}
