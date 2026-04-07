package com.example.easynote.repository;

import com.example.easynote.entity.CinRequest;
import com.example.easynote.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CinRequestRepository extends JpaRepository<CinRequest, Long> {
    Optional<CinRequest> findByNumeroDossier(String numeroDossier);
    List<CinRequest> findByStatut(RequestStatus statut);
    List<CinRequest> findByCitizenId(Long citizenId);
    long countByStatut(RequestStatus statut);

    @Query("SELECT MONTH(r.dateDepot), COUNT(r) FROM CinRequest r " +
           "WHERE YEAR(r.dateDepot) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(r.dateDepot) ORDER BY MONTH(r.dateDepot)")
    List<Object[]> countByMonth();

    @Query("SELECT r FROM CinRequest r WHERE r.statut = com.example.easynote.enums.RequestStatus.EN_ATTENTE AND r.dateDepot < :date")
    List<CinRequest> findRetarded(@Param("date") LocalDateTime date);
}
