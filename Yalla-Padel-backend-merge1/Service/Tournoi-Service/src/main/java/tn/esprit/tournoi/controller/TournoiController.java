package tn.esprit.tournoi.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tournoi.entity.Tournoi;
import tn.esprit.tournoi.service.ITournoiService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tournois")
public class TournoiController {

    private final ITournoiService tournoiService;

    // GET /tournois  (ce qu'Angular appelle)
    @GetMapping
    public List<Tournoi> getAllTournois() {
        return tournoiService.getAllTournois();
    }

    //  GET /tournois/{id}
    @GetMapping("/{id}")
    public Tournoi getTournoiById(@PathVariable Long id) {
        return tournoiService.getTournoiById(id);
    }

    // POST /tournois  (ce qu'Angular appelle)
    @PostMapping
    public Tournoi addTournoi(@RequestBody Tournoi tournoi) {
        return tournoiService.addTournoi(tournoi);
    }

    // PUT /tournois/{id}
    @PutMapping("/{id}")
    public Tournoi updateTournoi(@PathVariable Long id,
                                 @RequestBody Tournoi tournoi) {
        tournoi.setId(id);
        return tournoiService.updateTournoi(tournoi);
    }

    // DELETE /tournois/{id}
    @DeleteMapping("/{id}")
    public void deleteTournoi(@PathVariable Long id) {
        tournoiService.deleteTournoi(id);
    }

    // POST /tournois/{tournoiId}/inscrire — l'identité vient du JWT comme dans eventmodule
    @PostMapping("/{tournoiId}/inscrire")
    public Tournoi inscrireJoueur(@PathVariable Long tournoiId, @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String userEmail = jwt.getClaimAsString("email");
        return tournoiService.inscrireJoueur(tournoiId, userId, userEmail);
    }

    // POST /tournois/{tournoiId}/gagnant/{joueurId}
    @PostMapping("/{tournoiId}/gagnant/{joueurId}")
    public Tournoi designerGagnant(@PathVariable Long tournoiId, @PathVariable String joueurId) {
        return tournoiService.designerGagnant(tournoiId, joueurId);
    }
}
