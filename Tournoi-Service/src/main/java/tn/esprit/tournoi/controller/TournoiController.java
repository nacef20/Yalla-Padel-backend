package tn.esprit.tournoi.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tournoi.entity.Tournoi;
import tn.esprit.tournoi.service.ITournoiService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/tournois")
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

    // POST /tournois/{tournoiId}/inscrire/{joueurId}
    @PostMapping("/{tournoiId}/inscrire/{joueurId}")
    public Tournoi inscrireJoueur(@PathVariable Long tournoiId, @PathVariable Long joueurId) {
        return tournoiService.inscrireJoueur(tournoiId, joueurId);
    }

    // POST /tournois/{tournoiId}/gagnant/{joueurId}
    @PostMapping("/{tournoiId}/gagnant/{joueurId}")
    public Tournoi designerGagnant(@PathVariable Long tournoiId, @PathVariable Long joueurId) {
        return tournoiService.designerGagnant(tournoiId, joueurId);
    }
}
