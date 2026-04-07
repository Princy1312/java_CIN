package com.example.easynote.service;

import com.example.easynote.entity.User;
import com.example.easynote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;

    public List<User> getAll() { return repo.findAll(); }
    public Optional<User> getById(Long id) { return repo.findById(id); }
    public Optional<User> getByEmail(String email) { return repo.findByEmail(email); }
    public long count() { return repo.count(); }

    public User create(User u) {
        if (repo.existsByEmail(u.getEmail())) throw new RuntimeException("Email déjà utilisé: " + u.getEmail());
        u.setMotDePasse(encoder.encode(u.getMotDePasse()));
        return repo.save(u);
    }

    public User update(Long id, User data) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
        u.setNom(data.getNom()); u.setPrenom(data.getPrenom());
        u.setRole(data.getRole()); u.setActif(data.isActif());
        if (data.getMotDePasse() != null && !data.getMotDePasse().isBlank())
            u.setMotDePasse(encoder.encode(data.getMotDePasse()));
        return repo.save(u);
    }

    public void toggleStatus(Long id) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
        u.setActif(!u.isActif());
        repo.save(u);
    }

    public void delete(Long id) { repo.deleteById(id); }
}
