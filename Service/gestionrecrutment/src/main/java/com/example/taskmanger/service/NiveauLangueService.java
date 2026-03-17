package com.example.taskmanger.service;

import com.example.taskmanger.model.NiveauLangue;
import com.example.taskmanger.repository.NiveauLangueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NiveauLangueService {
    @Autowired
    private NiveauLangueRepository niveauLangueRepository;

    public List<NiveauLangue> getAll() {
        return niveauLangueRepository.findAll();
    }

    public Optional<NiveauLangue> getById(Long id) {
        return niveauLangueRepository.findById(id);
    }

    public NiveauLangue save(NiveauLangue niveauLangue) {
        return niveauLangueRepository.save(niveauLangue);
    }

    public void delete(Long id) {
        niveauLangueRepository.deleteById(id);
    }
} 