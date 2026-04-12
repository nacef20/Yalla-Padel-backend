package tn.esprit.eventmodule.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tn.esprit.eventmodule.dto.EnrichedEvenementDTO;
import tn.esprit.eventmodule.entity.Evenement;
import tn.esprit.eventmodule.service.EvenementService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    @PostMapping("/add")
    public ResponseEntity<Evenement> create(
            @Valid @RequestBody Evenement evenement,
            @AuthenticationPrincipal Jwt jwt) {
        // organizerId : the currently logged-in user
        evenement.setOrganizerId(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(evenementService.create(evenement));
    }

    @GetMapping("/find")
    public ResponseEntity<List<EnrichedEvenementDTO>> findAll() {
        return ResponseEntity.ok(evenementService.findAllEnriched());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrichedEvenementDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.findEnrichedById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<Evenement> update(@RequestParam Long id, @Valid @RequestBody Evenement evenement) {
        return ResponseEntity.ok(evenementService.update(id, evenement));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteById(@RequestParam Long id) {
        evenementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/available-seats")
    public ResponseEntity<Map<String, Integer>> getAvailableSeats(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("availableSeats", evenementService.getAvailableSeats(id)));
    }

    @GetMapping("/{id}/is-full")
    public ResponseEntity<Map<String, Boolean>> isFull(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("full", evenementService.isFull(id)));
    }
}
