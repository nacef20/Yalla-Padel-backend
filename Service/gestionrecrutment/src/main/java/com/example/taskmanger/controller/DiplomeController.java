package com.example.taskmanger.controller;

import com.example.taskmanger.model.Diplome;
import com.example.taskmanger.service.DiplomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diplome")
public class DiplomeController {
    @Autowired
    private DiplomeService diplomeService;

    @GetMapping("/all")
    public List<Diplome> getAll() {
        return diplomeService.getAll();
    }

    @GetMapping("/get/{id}")
    public Optional<Diplome> getById(@PathVariable Long id) {
        return diplomeService.getById(id);
    }

    @PostMapping("/add")
    public Diplome create(@RequestBody Diplome diplome) {
        return diplomeService.save(diplome);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        diplomeService.delete(id);
    }
} 