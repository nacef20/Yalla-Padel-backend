package com.example.taskmanger.controller;

import com.example.taskmanger.model.NiveauLangue;
import com.example.taskmanger.service.NiveauLangueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/niveau-langue")
public class NiveauLangueController {
    @Autowired
    private NiveauLangueService niveauLangueService;

    @GetMapping("/all")
    public List<NiveauLangue> getAll() {
        return niveauLangueService.getAll();
    }

    @GetMapping("/get/{id}")
    public Optional<NiveauLangue> getById(@PathVariable Long id) {
        return niveauLangueService.getById(id);
    }

    @PostMapping("/add")
    public NiveauLangue create(@RequestBody NiveauLangue niveauLangue) {
        return niveauLangueService.save(niveauLangue);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        niveauLangueService.delete(id);
    }
} 