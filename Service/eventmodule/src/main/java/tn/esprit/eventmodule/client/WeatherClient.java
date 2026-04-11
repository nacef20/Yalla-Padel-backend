package tn.esprit.eventmodule.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.esprit.eventmodule.dto.WeatherResponseDTO;

@FeignClient(name = "weatherClient", url = "https://api.open-meteo.com")
public interface WeatherClient {

    @GetMapping("/v1/forecast")
    WeatherResponseDTO getDailyWeather(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("daily") String dailyParams,
            @RequestParam("timezone") String timezone,
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate
    );
}
