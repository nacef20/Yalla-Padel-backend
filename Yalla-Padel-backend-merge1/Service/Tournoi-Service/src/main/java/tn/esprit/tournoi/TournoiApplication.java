package tn.esprit.tournoi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "tn.esprit.tournoi.feign")
public class TournoiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TournoiApplication.class, args);
    }
}