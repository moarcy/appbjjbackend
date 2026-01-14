package bjjapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "BJJ Backend");
        status.put("timestamp", Instant.now().toString());
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/robots.txt")
    public ResponseEntity<String> robots() {
        String robotsTxt = """
            User-agent: *
            Allow: /health
            Allow: /auth/
            Disallow: /admin/
            Disallow: /s/*/users/delete/

            Sitemap: https://appbjjbackend-staging.up.railway.app/sitemap.xml
            """;
        return ResponseEntity.ok()
            .header("Content-Type", "text/plain")
            .body(robotsTxt);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "BJJ Management API");
        info.put("status", "operational");
        info.put("docs", "/swagger-ui.html");
        info.put("health", "/health");
        return ResponseEntity.ok(info);
    }
}
