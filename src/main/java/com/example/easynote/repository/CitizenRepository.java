package com.example.easynote.repository;

import com.example.easynote.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByNumeroNational(String numeroNational);
    boolean existsByNumeroNational(String numeroNational);
    Optional<Citizen> findByQrCodeToken(String qrCodeToken);
    List<Citizen> findByArchiveFalse();

    @Query("SELECT c FROM Citizen c WHERE c.archive = false AND " +
           "(LOWER(c.nom) LIKE LOWER(CONCAT('%',:s,'%')) OR " +
           "LOWER(c.prenom) LIKE LOWER(CONCAT('%',:s,'%')) OR " +
           "c.numeroNational LIKE CONCAT('%',:s,'%'))")
    List<Citizen> searchCitizens(@Param("s") String s);

    @Query("SELECT c.region, COUNT(c) FROM Citizen c WHERE c.archive=false GROUP BY c.region")
    List<Object[]> countByRegion();

    long countByArchiveFalse();
}
