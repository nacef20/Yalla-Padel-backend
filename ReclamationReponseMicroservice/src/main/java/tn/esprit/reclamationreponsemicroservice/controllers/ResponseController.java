package tn.esprit.reclamationreponsemicroservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.reclamationreponsemicroservice.entities.Response;
import tn.esprit.reclamationreponsemicroservice.services.IResponseService;


@RestController
@RequestMapping("/responses")
public class ResponseController {

    private final IResponseService responseService;

    public ResponseController(IResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping
    public ResponseEntity<Response> createResponse(@RequestBody Response response) {
        Response createdResponse = responseService.createResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        return ResponseEntity.ok(responseService.getResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateResponse(@PathVariable Long id, @RequestBody Response response) {
        return ResponseEntity.ok(responseService.updateResponse(id, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        responseService.deleteResponse(id);
        return ResponseEntity.noContent().build();
    }
}
