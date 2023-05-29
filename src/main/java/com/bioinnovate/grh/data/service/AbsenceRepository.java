package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Absences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absences, Integer> {

    @Query(value = "SELECT SUM(duration) From absences WHERE MONTH(absences.date) = MONTH(CURRENT_TIMESTAMP) AND YEAR(absences.date) = YEAR(CURRENT_TIMESTAMP) AND justified = 0" ,
            nativeQuery = true)
    Double findAbsencesByMonth();

    @Modifying
    @Transactional
    @Query(value = "Delete From absences WHERE id = :absences_id" ,
            nativeQuery = true)
    void deleteAbsence(@Param("absences_id") int absencesId);
}
