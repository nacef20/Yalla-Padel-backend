package tn.esprit.tournoi.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tn.esprit.tournoi.dto.TerrainResponse;

@Component
@Slf4j
public class ReservationFeignClientFallback implements ReservationFeignClient {

    @Override
    public TerrainResponse getTerrainById(Long id) {
        log.warn("⚠️  terrain-service indisponible — fallback pour terrain ID={}", id);
        TerrainResponse fallback = new TerrainResponse();
        fallback.setId(id);
        fallback.setNom("Terrain inconnu (service indisponible)");
        fallback.setDisponible(false);
        return fallback;
    }

    @Override
    public void updateDisponibilite(Long terrainId, boolean disponible) {
        log.warn("⚠️  Impossible de mettre à jour terrain {} — terrain-service KO", terrainId);
    }
}