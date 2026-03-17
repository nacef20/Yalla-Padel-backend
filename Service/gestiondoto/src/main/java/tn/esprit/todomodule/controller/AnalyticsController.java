package tn.esprit.todomodule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.todomodule.dto.AnalyticsStudentDTO;
import tn.esprit.todomodule.dto.AnalyticsTeacherDTO;
import tn.esprit.todomodule.entity.User;
import tn.esprit.todomodule.repository.UserRepository;
import tn.esprit.todomodule.service.AnalyticsService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService, UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/student")
    public ResponseEntity<AnalyticsStudentDTO> getStudentStats() {
        // Fallback to any user since authentication is removed
        User student = userRepository.findAll().stream().findFirst().orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(analyticsService.getStudentStats(student));
    }

    @GetMapping("/teacher")
    public ResponseEntity<AnalyticsTeacherDTO> getTeacherStats() {
        return ResponseEntity.ok(analyticsService.getTeacherStats());
    }
}
