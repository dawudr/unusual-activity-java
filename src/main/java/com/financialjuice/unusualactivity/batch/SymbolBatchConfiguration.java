package com.financialjuice.unusualactivity.batch;

import com.financialjuice.unusualactivity.model.Symbol;
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
//spring boot configuration
//@EnableAutoConfiguration
// file that contains the properties
//@PropertySource("classpath:application.properties")
public class SymbolBatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SymbolBatchConfiguration.class);

    @Value("${database.driver}")
    private String databaseDriver;
    @Value("${database.url}")
    private String databaseUrl;
    @Value("${database.username}")
    private String databaseUsername;
    @Value("${database.password}")
    private String databasePassword;

    @Value("${csv.import.symbol.file}")
    private String symbolCsvFile;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public SymbolBatchConfiguration() {
        log.info("Constructor SymbolBatchConfiguration");
    }

    // tag::readerwriterprocessor[]
    /**
     * We define a bean that read each line of the input file.
     * @return
     */
    @Bean
    public ItemReader<Symbol> reader() {
        FlatFileItemReader<Symbol> reader = new FlatFileItemReader<Symbol>();
        reader.setResource(new ClassPathResource(symbolCsvFile));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<Symbol>() {{
            setLineTokenizer(new SymbolDelimitedLineTokenizer());
            // TODO: The exception is pretty self explanatory.  BeanWrapperFieldSetMapper allows for fuzzy matching on the names. You can configure how fuzzy it is via the distance. You can read more about setting that distance in the javadoc here: https://docs.spring.io/spring-batch/trunk/apidocs/org/springframework/batch/item/file/mapping/BeanWrapperFieldSetMapper.html#setDistanceLimit-int-
            // https://stackoverflow.com/questions/46836681/spring-batch-flatfileitemreader-token-is-of-type-object
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<Symbol>() {{
//                setTargetType(Symbol.class);
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
    public ItemProcessor<Symbol, Symbol> processor() {
        return new SymbolItemProcessor();
    }

    /**
     * Nothing special here a simple JpaItemWriter
     */
    @Bean
    public ItemWriter<Symbol> writer() {
//        JpaItemWriter writer = new JpaItemWriter<Symbol>();
//        writer.setEntityManagerFactory(entityManagerFactory().getObject());
        SymbolWriter writer = new SymbolWriter();
        return writer;

    }

    // tag::jobstep[]
    /**
     * This method declare the steps that the batch has to follow
     */
    @Bean
    public Job importSymbolJob(JobBuilderFactory jobs, Step s1) {
        log.info("Starting Import Symbol Job");
        return jobBuilderFactory.get("importSymbolJob")
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
                .<Symbol, Symbol> chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    // end::jobstep[]


    /**
     * As data source we use an external database
     *
     * @return
     */

//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(databaseDriver);
//        dataSource.setUrl(databaseUrl);
//        dataSource.setUsername(databaseUsername);
//        dataSource.setPassword(databasePassword);
//        return dataSource;
//    }
//
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//
//        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
//        lef.setPackagesToScan("com.financialjuice.unusualactivity.batch");
//        lef.setDataSource(dataSource());
//        lef.setJpaVendorAdapter(jpaVendorAdapter());
//        lef.setJpaProperties(new Properties());
//        return lef;
//    }
//
//
//    @Bean
//    public JpaVendorAdapter jpaVendorAdapter() {
//        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
//        jpaVendorAdapter.setDatabase(Database.MYSQL);
//        jpaVendorAdapter.setGenerateDdl(true);
//        jpaVendorAdapter.setShowSql(false);
//
//        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
//        return jpaVendorAdapter;
//    }


}
