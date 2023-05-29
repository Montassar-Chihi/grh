package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class ReclamationService extends CrudService<Reclamation, Integer> {
    private ReclamationRepository repository;

    public ReclamationService(@Autowired ReclamationRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ReclamationRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public List<Reclamation> findReclamationByEmployee(int id){return repository.findReclamationByEmployeeId(id);}

    public List<Reclamation> findReclamationByEmployeeAndMonth(int employeeId, int month){return repository.findReclamationByEmployeeAndMonth(employeeId,month);}

    public void deleteReclamation(int reclamationId){
        repository.deleteReclamation(reclamationId);
    }
}
