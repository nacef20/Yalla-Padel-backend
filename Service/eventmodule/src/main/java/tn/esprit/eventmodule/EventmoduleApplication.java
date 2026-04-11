package tn.esprit.eventmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EventmoduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventmoduleApplication.class, args);
    }

}
