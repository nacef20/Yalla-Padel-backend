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
    Tournoi inscrireJoueur(Long tournoiId, String userId, String userEmail);
    Tournoi desinscrireJoueur(Long tournoiId, String userId);
    Tournoi designerGagnant(Long tournoiId, String userId);
    List<Tournoi> getTournoisByStatut(String statut);
}