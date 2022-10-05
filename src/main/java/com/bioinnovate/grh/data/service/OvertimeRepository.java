package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OvertimeRepository extends JpaRepository<Overtime, Integer> {

    @Query(value = "SELECT * From overtime WHERE employee_id = :employee_id AND MONTH(overtime.date) = MONTH(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    List<Overtime> findOvertimeByEmployeeId(@Param("employee_id") int employeeId);

    @Query(value = "SELECT SUM(duration) From overtime WHERE employee_id = :employee_id AND MONTH(overtime.date) = MONTH(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    Integer findTotalOvertimeByEmployeeId(@Param("employee_id") int employeeId);

    @Modifying
    @Transactional
    @Query(value = "Delete From overtime WHERE id = :overtime_id" ,
            nativeQuery = true)
    void deleteOvertime(@Param("overtime_id") int overtime);
}
