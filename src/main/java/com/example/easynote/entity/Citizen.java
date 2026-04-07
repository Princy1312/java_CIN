package com.example.easynote.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "citizens")
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroNational;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Column(nullable = false)
    private String lieuNaissance;

    @Column(nullable = false)
    private String sexe;

    @Column(nullable = false)
    private String adresse;

    private String region;
    private String profession;
    private String photoPath;

    @Column(unique = true)
    private String qrCodeToken;

    private boolean archive = false;

    @Column(nullable = false)
    private LocalDateTime dateEnregistrement = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agentEnregistrement;

    // ========== CONSTRUCTEURS ==========
    public Citizen() {}

    public Citizen(Long id, String numeroNational, String nom, String prenom,
                   LocalDate dateNaissance, String lieuNaissance, String sexe,
                   String adresse, String region, String profession, String photoPath,
                   String qrCodeToken, boolean archive, LocalDateTime dateEnregistrement,
                   User agentEnregistrement) {
        this.id = id;
        this.numeroNational = numeroNational;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
        this.sexe = sexe;
        this.adresse = adresse;
        this.region = region;
        this.profession = profession;
        this.photoPath = photoPath;
        this.qrCodeToken = qrCodeToken;
        this.archive = archive;
        this.dateEnregistrement = dateEnregistrement;
        this.agentEnregistrement = agentEnregistrement;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getNumeroNational() { return numeroNational; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getLieuNaissance() { return lieuNaissance; }
    public String getSexe() { return sexe; }
    public String getAdresse() { return adresse; }
    public String getRegion() { return region; }
    public String getProfession() { return profession; }
    public String getPhotoPath() { return photoPath; }
    public String getQrCodeToken() { return qrCodeToken; }
    public boolean isArchive() { return archive; }
    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; }
    public User getAgentEnregistrement() { return agentEnregistrement; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setNumeroNational(String numeroNational) { this.numeroNational = numeroNational; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setRegion(String region) { this.region = region; }
    public void setProfession(String profession) { this.profession = profession; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setQrCodeToken(String qrCodeToken) { this.qrCodeToken = qrCodeToken; }
    public void setArchive(boolean archive) { this.archive = archive; }
    public void setDateEnregistrement(LocalDateTime dateEnregistrement) { this.dateEnregistrement = dateEnregistrement; }
    public void setAgentEnregistrement(User agentEnregistrement) { this.agentEnregistrement = agentEnregistrement; }
}