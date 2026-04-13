package tn.esprit.tournoi.service;

import tn.esprit.tournoi.entity.Tournoi;

import java.util.List;

public interface ITournoiService {

    // ---- CRUD ----
    Tournoi addTournoi(Tournoi tournoi);
    Tournoi updateTournoi(Tournoi tournoi);
    List<Tournoi> getAllTournois();
    Tournoi getTournoiById(Long id);
    void deleteTournoi(Long id);

    // ---- MÉTIER ----
    Tournoi inscrireJoueur(Long tournoiId, Long userId);
    Tournoi desinscrireJoueur(Long tournoiId, Long userId);
    Tournoi designerGagnant(Long tournoiId, Long userId);
    List<Tournoi> getTournoisByStatut(String statut);
}