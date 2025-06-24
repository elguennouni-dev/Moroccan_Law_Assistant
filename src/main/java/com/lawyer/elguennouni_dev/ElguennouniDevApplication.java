package com.lawyer.elguennouni_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.lawyer.elguennouni_dev.repository")
@EntityScan("com.lawyer.elguennouni_dev.entity")
public class ElguennouniDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElguennouniDevApplication.class, args);
	}

}
