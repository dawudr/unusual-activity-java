package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.SymbolData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class SymbolImporterJobConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SymbolImporterJobConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Value("${file.import.companies.lse}")
    public String fileName;

    public SymbolImporterJobConfiguration() {
        log.info("Constructor SymbolBatchConfiguration");
    }

    // tag::readerwriterprocessor[]
    /**
     * We define a bean that read each line of the input file.
     * @return
     */
    @Bean
    public ItemReader<SymbolData> reader() {

        FlatFileItemReader<SymbolData> reader = new FlatFileItemReader<SymbolData>();
        reader.setResource(new ClassPathResource(fileName));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<SymbolData>() {{
            setLineTokenizer(new SymbolDelimitedLineTokenizer());
            // TODO: The exception is pretty self explanatory.  BeanWrapperFieldSetMapper allows for fuzzy matching on the names. You can configure how fuzzy it is via the distance. You can read more about setting that distance in the javadoc here: https://docs.spring.io/spring-batch/trunk/apidocs/org/springframework/batch/item/file/mapping/BeanWrapperFieldSetMapper.html#setDistanceLimit-int-
            // https://stackoverflow.com/questions/46836681/spring-batch-flatfileitemreader-token-is-of-type-object
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<SymbolData>() {{
//                setTargetType(SymbolData.class);
//            }});
            setFieldSetMapper(new SymbolFieldSetMapper());
        }});
        return reader;
    }

    /**
     * The ItemProcessor is called after a new line is read and it allows the developer
     * to transform the data read
     */
    @Bean
    public ItemProcessor<SymbolData, SymbolData> processor() {
        return new SymbolItemProcessor();
    }

    /**
     * Nothing special here a simple JpaItemWriter
     */
    @Bean
    public ItemWriter<SymbolData> writer() {
//        JpaItemWriter writer = new JpaItemWriter<SymbolData>();
//        writer.setEntityManagerFactory(entityManagerFactory().getObject());
        SymbolWriter writer = new SymbolWriter();
        return writer;

    }

    // tag::jobstep[]
    /**
     * This method declare the steps that the batch has to follow
     */
    @Bean
    public Job job_SymbolImporter() {
        log.info("Starting Import SymbolData Job");



        return jobBuilderFactory.get("job_SymbolImporter")
                .incrementer(new RunIdIncrementer()) // because a spring config bug, this incrementer is not really useful
                .flow(step1())
                .end()
                .build();
    }

    /**
     * Step
     * We declare that every 1000 lines processed the data has to be committed
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<SymbolData, SymbolData> chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    // end::jobstep[]
}
