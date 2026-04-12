package tn.esprit.gestiongroupechat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestiongroupechat.Dto.JoinGroupDTO;
import tn.esprit.gestiongroupechat.entities.JoinGroup;
import tn.esprit.gestiongroupechat.entities.Status;
import tn.esprit.gestiongroupechat.service.IJoinGroupService;
import tn.esprit.gestiongroupechat.service.UserClient;

import java.util.List;

@RestController
@RequestMapping("/joinGroup")
@RequiredArgsConstructor

public class JoinGroupController {

    private final IJoinGroupService joinGroupService;
    private final UserClient userClient;

    @PostMapping("/add")
    public ResponseEntity<JoinGroupDTO> addRequest(@RequestBody @Valid JoinGroupDTO dto) {
        return ResponseEntity.ok(joinGroupService.addRequest(dto));
    }

    @GetMapping("/email/{id}")
    public String testFeign(@PathVariable String id) {
        return userClient.getUserEmailById(id);
    }



    @GetMapping("/status")
    public ResponseEntity<Status> getStatus(
            @RequestParam String userId,
            @RequestParam Long groupChatId) {

        Status status = joinGroupService.getStatusByUserAndGroup(userId, groupChatId);

        if (status == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(status);
    }



    @GetMapping("/group/{groupId}")
    public List<JoinGroup> getRequestsByGroup(@PathVariable Long groupId) {
        return joinGroupService.getDemandeByidgroup(groupId);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Void> accepterDemande(@PathVariable Long id) {
        joinGroupService.accepterDemande(id);


        return ResponseEntity.ok().build();


    }

    // Refuser une demande
    @PutMapping("/reject/{id}")
    public ResponseEntity<Void> refuserDemande(@PathVariable Long id) {
        joinGroupService.refuserDemande(id);
        return ResponseEntity.ok().build();
    }




}
