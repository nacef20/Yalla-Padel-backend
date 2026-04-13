package tn.esprit.tournoi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.esprit.tournoi.dto.TerrainResponse;

@FeignClient(
        name = "terrain-service",
        fallback = ReservationFeignClientFallback.class
)
public interface ReservationFeignClient {

    @GetMapping("/terrains/{id}")
    TerrainResponse getTerrainById(@PathVariable("id") Long id);

    @PutMapping("/terrains/{id}/disponibilite")
    void updateDisponibilite(
            @PathVariable("id") Long terrainId,
            @RequestParam("disponible") boolean disponible
    );
}