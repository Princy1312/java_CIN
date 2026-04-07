package com.example.easynote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class CinManagementApplication {

	@Value("${server.port:8080}")
	private int port;

	public static void main(String[] args) {
		SpringApplication.run(CinManagementApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onReady() {
		System.out.println("\n");
		System.out.println("╔══════════════════════════════════════════════════════╗");
		System.out.println("║         CIN MANAGEMENT - APPLICATION DÉMARRÉE        ║");
		System.out.println("╠══════════════════════════════════════════════════════╣");
		System.out.println("║                                                      ║");
		System.out.println("║   🌐  URL : http://localhost:" + port + "                     ║");
		System.out.println("║                                                      ║");
		System.out.println("╠══════════════════════════════════════════════════════╣");
		System.out.println("║   COMPTES PAR DÉFAUT :                               ║");
		System.out.println("║   admin@cin.gov        /  Admin@1234                 ║");
		System.out.println("║   agent@cin.gov        /  Agent@1234                 ║");
		System.out.println("║   validateur@cin.gov   /  Valid@1234                 ║");
		System.out.println("║   superviseur@cin.gov  /  Super@1234                 ║");
		System.out.println("╚══════════════════════════════════════════════════════╝");
		System.out.println("\n");
	}
}