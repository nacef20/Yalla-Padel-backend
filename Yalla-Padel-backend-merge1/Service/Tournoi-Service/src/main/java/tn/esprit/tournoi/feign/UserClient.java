package tn.esprit.tournoi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.tournoi.dto.UserResponse;

@FeignClient(name = "userservice", path = "/api/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") String id);

    @GetMapping("/{id}/email")
    String getUserEmailById(@PathVariable("id") String id);
}