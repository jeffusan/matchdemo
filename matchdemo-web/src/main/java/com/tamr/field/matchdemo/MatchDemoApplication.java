package com.tamr.field.matchdemo;

import com.tamr.field.batch.MapFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

@SpringBootApplication
@ComponentScan("com.tamr.field")
@EnableBatchProcessing
@ImportResource("classpath:springContext.xml")
public class MatchDemoApplication {

    @Value("#{'${inputfieldnames}'.split(',')}")
    private String[] fieldNames;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =  SpringApplication.run(MatchDemoApplication.class, args);

    }



    @Bean
    JdbcTemplate jdbcTemplate( DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    ;

    @Bean
    @StepScope
    public FlatFileItemReader<Map<String,String>> reader(@Value("#{jobParameters[FileName]}") String file) {

        MapFieldSetMapper fieldSetMapper = new MapFieldSetMapper();
        DefaultLineMapper mapper = new DefaultLineMapper();
        mapper.setFieldSetMapper(fieldSetMapper);
        DelimitedLineTokenizer dlt = new DelimitedLineTokenizer();
        dlt.setNames(fieldNames);
        mapper.setLineTokenizer(dlt);
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLinesToSkip(1);
        reader.setLineMapper(mapper);
        reader.setResource(new FileSystemResource(file));
        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        return reader;
    }

    @Bean
    public ItemProcessor processor(){
        return new PassThroughItemProcessor();
    }

    @Bean
    public Job importMatchJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("importMatchJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end().incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Map> reader,
                      ItemWriter<Map> writer, ItemProcessor<Map, Map> processor, StepExecutionListener notificationExecutionsListener) {
        return stepBuilderFactory.get("step1").listener(notificationExecutionsListener)
                .<Map, Map> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant().skip(Exception.class)
                .build();
    }
}
