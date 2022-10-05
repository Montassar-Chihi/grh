package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Delays;
import com.bioinnovate.grh.data.entity.Delays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DelaysRepository extends JpaRepository<Delays, Integer> {

    @Query(value = "SELECT * From delays WHERE employee_id = :employee_id AND MONTH(delays.date) = MONTH(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    List<Delays> findDelaysByEmployeeId(@Param("employee_id") int employeeId);

    @Query(value = "SELECT SUM(duration) From delays WHERE employee_id = :employee_id AND MONTH(delays.date) = MONTH(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    Integer findTotalDelaysByEmployeeId(@Param("employee_id") int employeeId);

    @Query(value = "SELECT SUM(duration) From delays WHERE employee_id = :employee_id AND MONTH(delays.date) = :month AND YEAR(delays.date) = YEAR(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    Double findDelaysByEmployeeAndMonth(@Param("employee_id") int employeeId,@Param("month") int month);

    @Modifying
    @Transactional
    @Query(value = "Delete From delays WHERE id = :delays_id" ,
            nativeQuery = true)
    void deleteDelays(@Param("delays_id") int delaysId);
}
