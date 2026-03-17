package com.example.taskmanger.controller;

import com.example.taskmanger.model.Experience;
import com.example.taskmanger.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/experience")
public class ExperienceController {
    @Autowired
    private ExperienceService experienceService;

    @GetMapping("/all")
    public List<Experience> getAll() {
        return experienceService.getAll();
    }

    @GetMapping("/get/{id}")
    public Optional<Experience> getById(@PathVariable Long id) {
        return experienceService.getById(id);
    }

    @PostMapping
    public Experience create(@RequestBody Experience experience) {
        return experienceService.save(experience);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        experienceService.delete(id);
    }
} 