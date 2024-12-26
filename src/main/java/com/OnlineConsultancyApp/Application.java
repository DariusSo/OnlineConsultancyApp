package com.OnlineConsultancyApp;

import com.OnlineConsultancyApp.config.Connect;
import com.OnlineConsultancyApp.models.Consultant;
import com.OnlineConsultancyApp.runnables.CheckTimesLeftRunnable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		Thread thread = new Thread(new CheckTimesLeftRunnable());
		thread.start();
	}

}
