package com.financialjuice.unusualactivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * | Annotation | Meaning                                             |
 * +------------+-----------------------------------------------------+
 * | @Component | generic stereotype for any Spring-managed component |
 * | @Repository| stereotype for persistence layer                    |
 * | @Service   | stereotype for service layer                        |
 * | @Controller| stereotype for presentation layer (spring-mvc)      |
 */

@EnableJpaAuditing
@EnableAutoConfiguration
@EnableAsync
//@EnableBatchProcessing
@SpringBootApplication
public class UnusualActivityApplication {

	public static void main(String[] args) {

		SpringApplication.run(UnusualActivityApplication.class, args);

	}

//	@Override
//	public void run(String... strings) throws Exception {
//		stockDataFeeder.start("RDSA.L", StockDataRestClient.TimeSeries.DAILY);
//	}


}
