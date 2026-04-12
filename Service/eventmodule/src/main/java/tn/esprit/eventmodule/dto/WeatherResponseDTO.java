package tn.esprit.eventmodule.dto;

import java.util.List;

public class WeatherResponseDTO {

    private Daily daily;

    public Daily getDaily() { return daily; }
    public void setDaily(Daily daily) { this.daily = daily; }

    public static class Daily {
        private List<Double> temperature_2m_max;
        private List<Integer> weathercode;

        public List<Double> getTemperature_2m_max() { return temperature_2m_max; }
        public void setTemperature_2m_max(List<Double> temperature_2m_max) { this.temperature_2m_max = temperature_2m_max; }
        
        public List<Integer> getWeathercode() { return weathercode; }
        public void setWeathercode(List<Integer> weathercode) { this.weathercode = weathercode; }
    }
}
