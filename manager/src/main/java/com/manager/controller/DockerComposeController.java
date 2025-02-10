package com.manager.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DockerComposeController {
	@GetMapping
	public String restratForm(Model model) {
		model.addAttribute("message", "go try!");
		return "docker-compose/restart";
	}
	
	@PostMapping
	public String restart(Model model) {
		String msg = "";
		try {
			// 운영 체제 감지
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                // Windows
                processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd .. && docker-compose down && docker-compose up -d --pull always");
            } else {
                // Linux 또는 macOS
                processBuilder = new ProcessBuilder("sh", "-c", "cd .. && docker-compose down && docker-compose up -d --pull always");
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // 완료될 때까지 대기

            msg = exitCode == 0 ? "Docker Compose restarted successfully!" : "Error restarting Docker Compose!";
        } catch (IOException | InterruptedException e) {
        	msg = "Error: " + e.getMessage();
        }
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        msg += "\n" + LocalDateTime.now().format(formatter);
		model.addAttribute("message", msg);
		return "docker-compose/restart";
	}
}
