package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Department;
import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class EmployeeService extends CrudService<Employee, Integer> {
    private EmployeeRepository repository;

    public EmployeeService(@Autowired EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected EmployeeRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.selectStillWorkingEmployees();
    }

    public List findAllForOneDepartment(Department department){
        return  repository.findAllForOneDepartment(department);
    }

    public Employee findEmployeeByEmail(String email){
        return repository.findEmployeeByEmail(email);
    }

    @Override
    public Employee update(Employee employee){
        if (employee.getUserRoles() == null){
            employee.setUserRoles(new UserRole(2));
        }
        employee.setActive(true);
        employee.setName(employee.getFirstName()+" "+employee.getLastName());
        return getRepository().save(employee);
    }

    public List<Employee> findEmployeeByFirstNameOrLastName(String name){return  repository.findEmployeeByFirstNameOrLastName(name,name);}

    public List<Employee> findSubs(String email, Department department){return repository.findSubs(email,department);}
}
