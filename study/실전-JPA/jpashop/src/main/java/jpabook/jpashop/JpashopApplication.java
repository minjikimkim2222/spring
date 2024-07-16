package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	@Bean
	Hibernate5JakartaModule hibernate5JakartaModule(){
		return new Hibernate5JakartaModule();
	}
	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}
