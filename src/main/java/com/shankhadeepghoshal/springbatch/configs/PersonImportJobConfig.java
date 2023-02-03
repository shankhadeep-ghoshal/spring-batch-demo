package com.shankhadeepghoshal.springbatch.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.springbatch.annotations.PersonImport;
import com.shankhadeepghoshal.springbatch.exceptions.ExceptionSkipPolicy;
import com.shankhadeepghoshal.springbatch.jobcompletionlisteners.PersonImportJobCompleteNotificationListener;
import com.shankhadeepghoshal.springbatch.pojos.Person;
import com.shankhadeepghoshal.springbatch.processors.PersonProcessor;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Configuration
public class PersonImportJobConfig {

    private static final int JOB_CHUNK_SIZE = 10;
    private static final String SQL_STRING =
            """
								INSERT INTO person (id, name, username, email, phone)
								VALUES (:id, :name, :username, :email, :phone)
						""";

    @Bean(destroyMethod = "")
    public JacksonJsonObjectReader<Person> objectReader(final ObjectMapper objectMapper) {
        final var jsonObjectReader = new JacksonJsonObjectReader<>(Person.class);
        jsonObjectReader.setMapper(objectMapper);

        return jsonObjectReader;
    }

    @Bean
    @PersonImport
    public JsonItemReader<Person> personReader(
            @Value("${job.api.url}") final String url,
            final JacksonJsonObjectReader<Person> objectReader) {
        return readPersonFromRestAndDumpToFile(url, objectReader);
    }

    @Bean
    @PersonImport
    public JdbcBatchItemWriter<Person> personWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(SQL_STRING)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    @PersonImport
    public Step personStep1(
            final JobRepository jobRepository,
            final PlatformTransactionManager txManager,
            @PersonImport final JdbcBatchItemWriter<Person> itemWriter,
            @PersonImport final JsonItemReader<Person> itemReader,
            @PersonImport final PersonProcessor itemProcessor,
            @PersonImport final PersonImportStepSkipListener stepSkipListener,
            final ExceptionSkipPolicy skipPolicy,
            @Value("${job.person.step.first}") final String stepName) {
        return new StepBuilder(stepName, jobRepository)
                .<Person, Person>chunk(JOB_CHUNK_SIZE, txManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .listener(stepSkipListener)
                .skipPolicy(skipPolicy)
                .build();
    }

    @Bean
    @PersonImport
    public Job personImportJob(
            final JobRepository jobRepository,
            @PersonImport
                    final PersonImportJobCompleteNotificationListener completeNotificationListener,
            @PersonImport final Step apiStep1,
            @Value("${job.person}") final String jobName) {
        return new JobBuilder(jobName, jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(completeNotificationListener)
                .flow(apiStep1)
                .end()
                .build();
    }

    @SneakyThrows
    private static JsonItemReader<Person> readPersonFromRestAndDumpToFile(
            final String url, final JacksonJsonObjectReader<Person> personObjectReader) {
        try (var iStream = new URL(url).openConnection().getInputStream()) {
            final var tmpDir = Paths.get(new ClassPathResource("data.json").getURI());
            final var writtenFileLoc =
                    Files.write(tmpDir, iStream.readAllBytes(), StandardOpenOption.WRITE);

            return new JsonItemReaderBuilder<Person>()
                    .jsonObjectReader(personObjectReader)
                    .resource(new FileUrlResource(writtenFileLoc.toFile().toURI().toURL()))
                    .name("studentJsonItemReader")
                    .build();
        }
    }
}
