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
        return Flux.create(sink -> Executors.newSingleThreadExecutor().submit(() -> {
            try {
                // 운영 체제 감지
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder processBuilder;

                if (os.contains("win")) {
                    processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd .. && docker-compose down && docker-compose up --pull always");
                } else {
                    processBuilder = new ProcessBuilder("sh", "-c", "cd .. && docker-compose down && docker-compose up --pull always");
                }

                // 프로세스 실행
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;

                // 표준 출력 로그
                while ((line = reader.readLine()) != null) {
                    sink.next(line + "\n"); // 개행 추가하여 줄 간격 문제 해결
                }

                // 에러 출력 로그
                while ((line = errorReader.readLine()) != null) {
                    sink.next("[ERROR] " + line + "\n"); // 에러 로그도 개행 포함
                }

                int exitCode = process.waitFor();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timestamp = LocalDateTime.now().format(formatter);

                if (exitCode == 0) {
                    sink.next("\n✅ Docker Compose restarted successfully! (" + timestamp + ")");
                } else {
                    sink.next("\n❌ Error restarting Docker Compose! (" + timestamp + ")");
                }

                sink.complete();
            } catch (IOException | InterruptedException e) {
                sink.error(e);
            }
        }));
    }
}
