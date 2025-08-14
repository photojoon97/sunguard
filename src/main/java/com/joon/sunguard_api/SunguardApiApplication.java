package com.joon.sunguard_api;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.net.URL;

@SpringBootApplication()
public class SunguardApiApplication {

	public static void main(String[] args) {
		URL resource = SunguardApiApplication.class.getClassLoader().getResource("application.yml");
		if (resource != null) {
			System.out.println(">> Found application.yml at: " + resource.getPath());
		} else {
			System.out.println(">> application.yml NOT found on classpath");
		}

		SpringApplication.run(SunguardApiApplication.class, args);


	}

}
