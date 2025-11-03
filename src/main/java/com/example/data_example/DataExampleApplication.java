package com.example.data_example;

import com.example.data_example.config.Authorities;
import com.example.data_example.config.RsaKeyProperties;
import com.example.data_example.domain.User;
import com.example.data_example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class DataExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataExampleApplication.class, args);
	}


    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.save(new User("user1", passwordEncoder.encode("password"), Authorities.ROLE_USER));
            userRepository.save(new User("user2", passwordEncoder.encode("password"), Authorities.ROLE_USER));
            userRepository.save(new User("admin", passwordEncoder.encode("password"), String.join(",", List.of(Authorities.ROLE_USER, Authorities.ROLE_ADMIN))));
        };
    }
}
