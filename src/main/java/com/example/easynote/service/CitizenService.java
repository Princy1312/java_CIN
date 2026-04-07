package com.example.easynote.service;

import com.example.easynote.entity.*;
import com.example.easynote.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class CitizenService {
    @Autowired private CitizenRepository repo;
    @Autowired private QrCodeService qrCodeService;
    @Value("${app.upload.dir}") private String uploadDir;

    public List<Citizen> getAll() { return repo.findByArchiveFalse(); }
    public Optional<Citizen> getById(Long id) { return repo.findById(id); }
    public Optional<Citizen> getByNumeroNational(String n) { return repo.findByNumeroNational(n); }
    public Optional<Citizen> verifyByQrCode(String token) { return repo.findByQrCodeToken(token); }
    public List<Citizen> search(String s) { return repo.searchCitizens(s); }
    public long count() { return repo.countByArchiveFalse(); }
    public List<Object[]> countByRegion() { return repo.countByRegion(); }

    public Citizen create(Citizen c, MultipartFile photo, User agent) throws Exception {
        if (repo.existsByNumeroNational(c.getNumeroNational()))
            throw new RuntimeException("Numéro national déjà enregistré");
        if (photo != null && !photo.isEmpty()) c.setPhotoPath(saveFile(photo));
        c.setQrCodeToken(qrCodeService.generateToken());
        c.setAgentEnregistrement(agent);
        return repo.save(c);
    }

    public Citizen update(Long id, Citizen data, MultipartFile photo) throws IOException {
        Citizen c = repo.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
        c.setNom(data.getNom()); c.setPrenom(data.getPrenom());
        c.setDateNaissance(data.getDateNaissance()); c.setLieuNaissance(data.getLieuNaissance());
        c.setSexe(data.getSexe()); c.setAdresse(data.getAdresse());
        c.setRegion(data.getRegion()); c.setProfession(data.getProfession());
        if (photo != null && !photo.isEmpty()) c.setPhotoPath(saveFile(photo));
        return repo.save(c);
    }

    public void archive(Long id) {
        Citizen c = repo.findById(id).orElseThrow(() -> new RuntimeException("Introuvable"));
        c.setArchive(true); repo.save(c);
    }

    public void delete(Long id) { repo.deleteById(id); }

    private String saveFile(MultipartFile f) throws IOException {
        // Dossier uploads/ à la racine du projet (pas dans Tomcat temp)
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "photos");
        Files.createDirectories(uploadPath);

        // Nettoyer le nom de fichier : supprimer espaces et caractères spéciaux
        String originalName = f.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        // Nom final = UUID + extension uniquement (pas de nom original)
        String cleanName = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(cleanName);
        Files.write(filePath, f.getBytes());

        return cleanName;
    }
}