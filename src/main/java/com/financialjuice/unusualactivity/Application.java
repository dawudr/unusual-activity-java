package com.financialjuice.unusualactivity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

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
@SpringBootApplication
@EnableScheduling
@Configuration
@ComponentScan("com.financialjuice.unusualactivity.*")
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

//	@Override
//	public void run(String... strings) throws Exception {
//		stockDataFeeder.start("RDSA.L", StockDataRestClient.TimeSeries.DAILY);
//	}


}
