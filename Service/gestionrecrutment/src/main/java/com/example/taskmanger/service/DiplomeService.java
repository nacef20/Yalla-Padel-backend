package com.example.taskmanger.service;

import com.example.taskmanger.model.Diplome;
import com.example.taskmanger.repository.DiplomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiplomeService {
    @Autowired
    private DiplomeRepository diplomeRepository;

    public List<Diplome> getAll() {
        return diplomeRepository.findAll();
    }

    public Optional<Diplome> getById(Long id) {
        return diplomeRepository.findById(id);
    }

    public Diplome save(Diplome diplome) {
        return diplomeRepository.save(diplome);
    }

    public void delete(Long id) {
        diplomeRepository.deleteById(id);
    }
} 