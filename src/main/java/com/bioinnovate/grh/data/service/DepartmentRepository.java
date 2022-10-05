package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Department getDepartmentByName(String name);

    @Modifying
    @Transactional
    @Query(value = "Delete From department WHERE id = :department_id" ,
            nativeQuery = true)
    void deleteDepartment(@Param("department_id") int departmentId);
}
