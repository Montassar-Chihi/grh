package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.DaysOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DaysOffRepository extends JpaRepository<DaysOff, Integer> {

    List<DaysOff> findDaysOffByEmployeeId(int id);

    @Modifying
    @Transactional
    @Query(value = "Delete From days_off WHERE id = :daysOff_id" ,
            nativeQuery = true)
    void deleteDaysOff(@Param("daysOff_id") int daysOffId);
}
