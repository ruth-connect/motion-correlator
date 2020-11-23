package uk.me.ruthmills.motioncorrelator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MotionCorrelatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotionCorrelatorApplication.class, args);
	}
}
