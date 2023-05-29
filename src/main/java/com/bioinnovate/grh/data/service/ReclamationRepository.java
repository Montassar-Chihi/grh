package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Integer> {

    @Query(value = "SELECT * From reclamation WHERE employee_id = :employee_id AND MONTH(reclamation.date) = :month AND YEAR(reclamation.date) = YEAR(CURRENT_TIMESTAMP) AND justified = 0" ,
            nativeQuery = true)
    List<Reclamation> findReclamationByEmployeeAndMonth(@Param("employee_id") int employeeId,@Param("month") int month);

    List<Reclamation> findReclamationByEmployeeId(int id);

    @Modifying
    @Transactional
    @Query(value = "Delete From reclamation WHERE id = :reclamation_id" ,
            nativeQuery = true)
    void deleteReclamation(@Param("reclamation_id") int reclamationId);
}
