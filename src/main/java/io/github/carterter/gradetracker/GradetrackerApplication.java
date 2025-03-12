package io.github.carterter.gradetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GradetrackerApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(GradetrackerApplication.class, args);
	}
}
