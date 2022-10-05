package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.RequestDayOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface RequestDayOffRepository extends JpaRepository<RequestDayOff, Integer> {

    List<RequestDayOff> findRequestDayOffByEmployeeId(int id);

    List<RequestDayOff> findRequestDayOffByDepartmentId(int id);

    List<RequestDayOff> findRequestDayOffByDepartmentIdAndDateBegin(int id, Date date);

    @Modifying
    @Transactional
    @Query(value = "Delete From request_day_off WHERE id = :request_day_off_id" ,
            nativeQuery = true)
    void deleteRequestDayOff(@Param("request_day_off_id") int requestDayOffId);
}
