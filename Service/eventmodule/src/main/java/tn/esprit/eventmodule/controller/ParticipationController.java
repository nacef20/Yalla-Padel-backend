package tn.esprit.eventmodule.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tn.esprit.eventmodule.entity.Participation;
import tn.esprit.eventmodule.service.ParticipationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping("/add")
    public ResponseEntity<Participation> create(
            @Valid @RequestBody Participation participation,
            @AuthenticationPrincipal Jwt jwt) {
        // userId : the logged-in user
        participation.setUserId(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(participationService.create(participation));
    }

    @GetMapping("/findall")
    public ResponseEntity<List<Participation>> findAll() {
        return ResponseEntity.ok(participationService.findAll());
    }

    @GetMapping("/find")
    public ResponseEntity<Participation> findById(@RequestParam Long id) {
        return ResponseEntity.ok(participationService.findById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Participation> update(@RequestParam Long id,
            @Valid @RequestBody Participation participation) {
        return ResponseEntity.ok(participationService.update(id, participation));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteById(@RequestParam Long id) {
        participationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Participation> registerToEvent(
            @RequestBody Map<String, Long> body,
            @AuthenticationPrincipal Jwt jwt) {
        // userId comes from the JWT token — the client only sends eventId
        String userId = jwt.getSubject();
        Long eventId = body.get("eventId");
        if (eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(participationService.registerToEvent(userId, eventId));
    }

    @DeleteMapping("/unregister")
    public ResponseEntity<Void> unregisterFromEvent(@RequestParam Long id) {
        participationService.unregisterFromEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkin")
    public ResponseEntity<Participation> checkIn(@RequestParam Long id) {
        return ResponseEntity.ok(participationService.checkIn(id));
    }
}
