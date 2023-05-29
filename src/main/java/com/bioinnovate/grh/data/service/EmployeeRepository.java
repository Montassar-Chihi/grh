package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Department;
import com.bioinnovate.grh.data.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {


    @Query(value = "SELECT * FROM `user_info` WHERE first_name LIKE %:first_name% OR last_name LIKE %:last_name% " ,
            nativeQuery = true)
    List<Employee> findEmployeeByFirstNameOrLastName(@Param("first_name") String firstName,
                                                     @Param("last_name") String lastName);

    @Query(value = "SELECT COUNT(*) FROM `user_info` WHERE is_in_days_off = 1 " ,
            nativeQuery = true)
    Integer findEmployeesInDaysOff();

    @Query(value = "SELECT * FROM `user_info` WHERE email != :email AND department_id = :department_id " ,
            nativeQuery = true)
    List<Employee> findSubs(@Param("email") String email , @Param("department_id") Department department);

    @Query(value = "SELECT * FROM `user_info` WHERE active = 1 AND user_info.user_roles_id != 4",
            nativeQuery = true)
    List<Employee> selectStillWorkingEmployees();

    @Query(value = "SELECT * FROM `user_info` WHERE active = 1 AND department_id = :department_id AND user_info.user_roles_id != 3 ",
            nativeQuery = true)
    List<Employee> findAllForOneDepartment( @Param("department_id") Department department);

    @Query(value = "SELECT * FROM `user_info` WHERE active = 1 AND department_id = :department_id ",
            nativeQuery = true)
    List<Employee> findAllEmployeesForOneDepartment( @Param("department_id") Department department);

    Employee findEmployeeByEmail(String email);
}
