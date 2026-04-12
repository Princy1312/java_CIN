package com.example.easynote.service;

import com.example.easynote.entity.*;
import com.example.easynote.enums.*;
import com.example.easynote.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CinRequestService {
    @Autowired private CinRequestRepository repo;
    @Autowired private CitizenRepository citizenRepo;

    public List<CinRequest> getAll() { return repo.findAll(); }
    public Optional<CinRequest> getById(Long id) { return repo.findById(id); }
    public List<CinRequest> getByStatus(RequestStatus s) { return repo.findByStatut(s); }
    public long countByStatus(RequestStatus s) { return repo.countByStatut(s); }
    public List<Object[]> getMonthlyStats() { return repo.countByMonth(); }
    public List<CinRequest> getRetarded() { return repo.findRetarded(LocalDateTime.now().minusDays(7)); }

    public CinRequest create(Long citizenId, RequestType type, User agent) {
        Citizen c = citizenRepo.findById(citizenId).orElseThrow(() -> new RuntimeException("Citoyen introuvable"));
        CinRequest r = new CinRequest();
        r.setNumeroDossier("DOS-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        r.setCitizen(c); r.setType(type);
        r.setStatut(RequestStatus.EN_ATTENTE); r.setAgentResponsable(agent);
        return repo.save(r);
    }

    public CinRequest updateStatus(Long id, RequestStatus status, String motif, User agent) {
        System.out.println("Mise à jour du statut de la demande " + id + " vers: " + status);
        
        CinRequest r = repo.findById(id).orElseThrow(() -> new RuntimeException("Demande introuvable"));
        System.out.println("Demande trouvée - Statut actuel: " + r.getStatut());
        
        r.setStatut(status);
        r.setDateTraitement(LocalDateTime.now());
        
        if (motif != null && !motif.isBlank()) {
            r.setMotifRejet(motif);
            System.out.println("Motif de rejet: " + motif);
        }
        
        if (agent != null) {
            r.setAgentResponsable(agent);
            System.out.println("Agent responsable: " + agent.getEmail());
        }
        
        CinRequest saved = repo.save(r);
        System.out.println("Demande mise à jour avec succès - Nouveau statut: " + saved.getStatut());
        
        return saved;
    }
}
