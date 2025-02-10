package com.manager.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DockerComposeController {

    @GetMapping(value = "/restart-docker", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> restartDocker() {
        return executeDockerCommand("docker-compose down && docker-compose up --pull always");
    }

    @GetMapping(value = "/docker-down", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> dockerDown() {
        return executeDockerCommand("docker-compose down");
    }

    @GetMapping(value = "/docker-up", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> dockerUp() {
        return executeDockerCommand("docker-compose up -d --pull always");
    }

    private Flux<String> executeDockerCommand(String command) {
        return Flux.create(sink -> Executors.newSingleThreadExecutor().submit(() -> {
            try {
                // 운영 체제 감지
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder processBuilder;

                if (os.contains("win")) {
                    processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd .. && " + command);
                } else {
                    processBuilder = new ProcessBuilder("sh", "-c", "cd .. && " + command);
                }

                // 프로세스 실행
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;

                // 표준 출력 로그
                while ((line = reader.readLine()) != null) {
                    sink.next(line + "\n");
                }

                // 에러 출력 로그
                while ((line = errorReader.readLine()) != null) {
                    sink.next("[ERROR] " + line + "\n");
                }

                int exitCode = process.waitFor();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = LocalDateTime.now().format(formatter);

                if (exitCode == 0) {
                    sink.next("\n✅ Command executed successfully! (" + timestamp + ")");
                } else {
                    sink.next("\n❌ Error executing command! (" + timestamp + ")");
                }

                sink.complete();
            } catch (IOException | InterruptedException e) {
                sink.error(e);
            }
        }));
    }
}
