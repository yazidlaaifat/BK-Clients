package org.app.demokeyclock;

import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.repositories.ClientRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class DemoKeyclockApplication implements CommandLineRunner {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importClientJob;

    public static void main(String[] args) {
        SpringApplication.run(DemoKeyclockApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Use unique job parameters on each run (e.g., current timestamp)
        JobExecution execution = jobLauncher.run(importClientJob,
                new JobParametersBuilder()
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters());
        System.out.println("Job Status : " + execution.getStatus());
    }


}
