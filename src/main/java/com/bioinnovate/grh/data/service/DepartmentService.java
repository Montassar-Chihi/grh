package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;
@Service
public class DepartmentService extends CrudService<Department, Integer> {
    private DepartmentRepository repository;

    public DepartmentService(@Autowired DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DepartmentRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public void deleteDepartment(int departmentId){
        repository.deleteDepartment(departmentId);
    }

    public Department getDepartmentByName(String name){
        return repository.getDepartmentByName(name);
    }
}
