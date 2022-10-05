package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Overtime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class OvertimeService extends CrudService<Overtime, Integer> {
    private OvertimeRepository repository;

    public OvertimeService(@Autowired OvertimeRepository repository) {
        this.repository = repository;
    }

    @Override
    protected OvertimeRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public List<Overtime> findOvertimeByEmployee(int id){return repository.findOvertimeByEmployeeId(id);}

    public Integer findTotalOvertimeByEmployee(int id){return repository.findTotalOvertimeByEmployeeId(id);}

    public void deleteOvertime(int delaysId){
        repository.deleteOvertime(delaysId);
    }
}

