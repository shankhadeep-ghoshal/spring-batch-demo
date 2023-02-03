package com.shankhadeepghoshal.springbatch.purchaseimport;

import com.shankhadeepghoshal.springbatch.ExceptionSkipPolicy;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Configuration
public class PurchaseImportJobConfig {

  private static final int JOB_CHUNK_SIZE = 10;
  private static final int LINES_TO_SKIP = 1;

  private static final String SQL_STRING =
      """
					INSERT into person_purchase (cust_id, amount, buy_date)
					VALUES (:custId, :amount, :buyDate)
				""";

  @Bean
  @PurchaseImport
  public ConversionService createConversionService() {
    final var conversionService = new DefaultConversionService();
    DefaultConversionService.addDefaultConverters(conversionService);
    // lambda cannot infer types
    //noinspection Convert2Lambda
    conversionService.addConverter(
        new Converter<String, LocalDate>() {
          @Override
          public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
          }
        });

    return conversionService;
  }

  @Bean
  @PurchaseImport
  public FieldSetMapper<Purchase> testClassRowMapper(@PurchaseImport final ConversionService cs) {
    final var mapper = new BeanWrapperFieldSetMapper<Purchase>();
    mapper.setConversionService(cs);
    mapper.setTargetType(Purchase.class);

    return mapper;
  }

  @Bean
  @PurchaseImport
  public FlatFileItemReader<Purchase> reader(@PurchaseImport final FieldSetMapper<Purchase> fsm) {
    return new FlatFileItemReaderBuilder<Purchase>()
        .name("personItemReader")
        .resource(new ClassPathResource("purchase_record.csv"))
        .linesToSkip(LINES_TO_SKIP)
        .delimited()
        .names("custId", "amount", "buyDate")
        .fieldSetMapper(fsm)
        .build();
  }

  @Bean
  @PurchaseImport
  public JdbcBatchItemWriter<Purchase> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Purchase>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql(SQL_STRING)
        .dataSource(dataSource)
        .build();
  }

  @Bean
  @PurchaseImport
  public Step purchaseStep1(
      final JobRepository jobRepository,
      final PlatformTransactionManager txManager,
      @PurchaseImport final JdbcBatchItemWriter<Purchase> itemWriter,
      @PurchaseImport final FlatFileItemReader<Purchase> itemReader,
      @PurchaseImport final PurchaseProcessor itemProcessor,
      @PurchaseImport final PurchaseImportSkipListener stepSkipListener,
      final ExceptionSkipPolicy skipPolicy,
      @Value("${job.purchase.step.first}") final String stepName) {
    return new StepBuilder(stepName, jobRepository)
        .<Purchase, Purchase>chunk(JOB_CHUNK_SIZE, txManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .faultTolerant()
        .listener(stepSkipListener)
        .skipPolicy(skipPolicy)
        .build();
  }

  @Bean
  @PurchaseImport
  public Job purchaseImportJob(
      final JobRepository jobRepository,
      @PurchaseImport
          final PurchaseImportCompletionNotificationListener completeNotificationListener,
      @PurchaseImport final Step purchaseStep1,
      @Value("${job.purchase}") final String jobName) {
    return new JobBuilder(jobName, jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(completeNotificationListener)
        .flow(purchaseStep1)
        .end()
        .build();
  }
}
