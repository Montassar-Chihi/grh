package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Absences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absences, Integer> {

    @Query(value = "SELECT SUM(duration) From absences WHERE employee_id = :employee_id AND MONTH(absences.date) = :month AND YEAR(absences.date) = YEAR(CURRENT_TIMESTAMP) " ,
            nativeQuery = true)
    Double findAbsencesByEmployeeAndMonth(@Param("employee_id") int employeeId,@Param("month") int month);

    List<Absences> findAbsencesByEmployeeId(int id);

    @Modifying
    @Transactional
    @Query(value = "Delete From absences WHERE id = :absences_id" ,
            nativeQuery = true)
    void deleteAbsence(@Param("absences_id") int absencesId);
}
