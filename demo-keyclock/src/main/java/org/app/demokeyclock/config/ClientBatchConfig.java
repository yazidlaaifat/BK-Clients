package org.app.demokeyclock.config;

import org.app.demokeyclock.entities.Client;
import org.app.demokeyclock.services.ClientProcessor;
import org.app.demokeyclock.services.JobCompletionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
@Configuration
@EnableBatchProcessing
public class ClientBatchConfig {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    public boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }


    @Bean
    public FlatFileItemReader<Client> reader() {
        String file = "C:/Users/elyaz/Desktop/doc/client.txt";
        if (fileExists(file)) {
            System.out.println("File exists and is accessible.");
        } else {
            System.out.println("File not found or not accessible.");
        }
        return new FlatFileItemReaderBuilder<Client>()
                .name("clientItemReader")
                .resource(new FileSystemResource(file))
                .delimited()
                .delimiter("|")
                .names("cin", "adresse", "nom", "prenom", "telephone")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Client.class);
                }})
                .build();
    }
    @Bean
    public JdbcBatchItemWriter<Client> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Client>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO client (cin, adresse, nom, prenom, telephone) " +
                        "VALUES (:cin, :adresse, :nom, :prenom, :telephone) " +
                        "ON DUPLICATE KEY UPDATE adresse = VALUES(adresse), nom = VALUES(nom), prenom = VALUES(prenom), telephone = VALUES(telephone);")

                .dataSource(dataSource)
                .build();
    }

    @Bean
    protected Step maskingStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<Client> reader,
                               ClientProcessor clientProcces,
                               JdbcBatchItemWriter<Client> writer) {
        return new StepBuilder("maskingStep", jobRepository)
                .<Client, Client>chunk(10, transactionManager)
                .reader(reader)
                .processor(clientProcces)
                .writer(writer)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(ValidationException.class)
                .skipLimit(100)
                .build();
    }
    @Bean
    public Job importClientJob(JobCompletionListener listener, Step maskingStep, JobRepository jobRepository) {
        return new JobBuilder("importClientJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(maskingStep)

                .end()

                .build();
    }
}
