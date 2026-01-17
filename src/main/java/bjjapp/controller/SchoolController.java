package bjjapp.controller;

import bjjapp.entity.School;
import bjjapp.entity.School.SchoolStatus;
import bjjapp.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "https://appbjj.com.br", "https://appbjjfront-hvhk.vercel.app/"})
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<School>> findAll() {
        List<School> schools = schoolService.findAll();
        return ResponseEntity.ok(schools);
    }

    @PostMapping
    public ResponseEntity<School> create(@RequestBody School school) {
        School created = schoolService.create(school);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> update(@PathVariable Long id, @RequestBody School school) {
        School updated = schoolService.update(id, school);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<School> activate(@PathVariable Long id) {
        School activated = schoolService.activate(id);
        return ResponseEntity.ok(activated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<School> deactivate(@PathVariable Long id) {
        School deactivated = schoolService.deactivate(id);
        return ResponseEntity.ok(deactivated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long id) {
        School school = schoolService.findById(id);
        Map<String, Object> status = Map.of(
            "id", school.getId(),
            "name", school.getName(),
            "slug", school.getSlug(),
            "status", school.getStatus(),
            "phone", school.getPhone(),
            "trialStart", school.getCreatedAt(),
            "trialEnd", school.getCreatedAt().plusDays(30), // Exemplo: 30 dias trial
            "isExpired", school.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now()),
            "createdAt", school.getCreatedAt(),
            "updatedAt", school.getUpdatedAt()
        );
        return ResponseEntity.ok(status);
    }
}
