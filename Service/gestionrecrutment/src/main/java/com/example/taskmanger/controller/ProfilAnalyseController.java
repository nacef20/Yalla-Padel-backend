package com.example.taskmanger.controller;

import com.example.taskmanger.model.ProfilAnalyse;
import com.example.taskmanger.service.ProfilAnalyseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profil-analyse")
public class ProfilAnalyseController {
    @Autowired
    private ProfilAnalyseService profilAnalyseService;

    @GetMapping("/all")
    public List<ProfilAnalyse> getAll() {
        return profilAnalyseService.getAll();
    }

    @GetMapping("/get/{id}")
    public Optional<ProfilAnalyse> getById(@PathVariable Long id) {
        return profilAnalyseService.getById(id);
    }

    @PostMapping("/add")
    public ProfilAnalyse create(@RequestBody ProfilAnalyse profilAnalyse) {
        return profilAnalyseService.save(profilAnalyse);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        profilAnalyseService.delete(id);
    }
} 