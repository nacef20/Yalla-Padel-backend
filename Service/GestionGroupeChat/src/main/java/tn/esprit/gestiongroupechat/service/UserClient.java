package tn.esprit.gestiongroupechat.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "userservice", url = "http://localhost:2020")
public interface UserClient {

    @GetMapping("api/users/{id}/email")
    String getUserEmailById(@PathVariable String id);

}
