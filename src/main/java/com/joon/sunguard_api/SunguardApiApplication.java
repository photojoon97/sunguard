package com.joon.sunguard_api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.net.URL;

@SpringBootApplication()
public class SunguardApiApplication {

	static {
		// 네이티브 전송 비활성화
		System.setProperty("io.netty.resolver.dns.preferNativeTransport", "false");
	}

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
