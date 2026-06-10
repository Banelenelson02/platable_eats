package com.plateable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlateableEatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlateableEatsApplication.class, args);
		System.out.println("\n=========================================");
		System.out.println("🚀 PLATEABLE EATS SERVER IS RUNNING!");
		System.out.println("👉 Go to: http://localhost:8080");
		System.out.println("=========================================\n");
	}
}