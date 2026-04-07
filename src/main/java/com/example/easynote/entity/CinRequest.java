package com.example.easynote.entity;

import com.example.easynote.enums.RequestStatus;
import com.example.easynote.enums.RequestType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cin_requests")
public class CinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroDossier;

    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus statut = RequestStatus.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agentResponsable;

    private String motifRejet;
    private String observations;

    @Column(nullable = false)
    private LocalDateTime dateDepot = LocalDateTime.now();

    private LocalDateTime dateTraitement;

    // ========== CONSTRUCTEURS ==========
    public CinRequest() {}

    public CinRequest(Long id, String numeroDossier, Citizen citizen, RequestType type,
                      RequestStatus statut, User agentResponsable, String motifRejet,
                      String observations, LocalDateTime dateDepot, LocalDateTime dateTraitement) {
        this.id = id;
        this.numeroDossier = numeroDossier;
        this.citizen = citizen;
        this.type = type;
        this.statut = statut;
        this.agentResponsable = agentResponsable;
        this.motifRejet = motifRejet;
        this.observations = observations;
        this.dateDepot = dateDepot;
        this.dateTraitement = dateTraitement;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getNumeroDossier() { return numeroDossier; }
    public Citizen getCitizen() { return citizen; }
    public RequestType getType() { return type; }
    public RequestStatus getStatut() { return statut; }
    public User getAgentResponsable() { return agentResponsable; }
    public String getMotifRejet() { return motifRejet; }
    public String getObservations() { return observations; }
    public LocalDateTime getDateDepot() { return dateDepot; }
    public LocalDateTime getDateTraitement() { return dateTraitement; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setNumeroDossier(String numeroDossier) { this.numeroDossier = numeroDossier; }
    public void setCitizen(Citizen citizen) { this.citizen = citizen; }
    public void setType(RequestType type) { this.type = type; }
    public void setStatut(RequestStatus statut) { this.statut = statut; }
    public void setAgentResponsable(User agentResponsable) { this.agentResponsable = agentResponsable; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
    public void setObservations(String observations) { this.observations = observations; }
    public void setDateDepot(LocalDateTime dateDepot) { this.dateDepot = dateDepot; }
    public void setDateTraitement(LocalDateTime dateTraitement) { this.dateTraitement = dateTraitement; }
}