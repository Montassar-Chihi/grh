package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Absences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class AbsenceService extends CrudService<Absences, Integer> {
    private AbsenceRepository repository;

    public AbsenceService(@Autowired AbsenceRepository repository) {
        this.repository = repository;
    }

    @Override
    protected AbsenceRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public List<Absences> findAbsencesByEmployee(int id){return repository.findAbsencesByEmployeeId(id);}

    public Double findAbsencesByEmployeeAndMonth(int employeeId, int month){return repository.findAbsencesByEmployeeAndMonth(employeeId,month);}

    public void deleteAbsence(int absencesId){
        repository.deleteAbsence(absencesId);
    }
}
