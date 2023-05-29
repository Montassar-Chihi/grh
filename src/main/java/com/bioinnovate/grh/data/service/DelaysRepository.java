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

    @Query(value = "SELECT SUM(duration) FROM delays WHERE MONTH(delays.date) = MONTH(CURRENT_TIMESTAMP)" ,
            nativeQuery = true)
    Integer findTotalDelays();

    @Modifying
    @Transactional
    @Query(value = "Delete From delays WHERE id = :delays_id" ,
            nativeQuery = true)
    void deleteDelays(@Param("delays_id") int delaysId);
}
