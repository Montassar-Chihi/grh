package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.DaysOff;
import com.bioinnovate.grh.data.entity.DaysOff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class DaysOffService extends CrudService<DaysOff, Integer> {
    private DaysOffRepository repository;

    public DaysOffService(@Autowired DaysOffRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DaysOffRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }
    public List<DaysOff> findDaysOffByEmployee(int id){return repository.findDaysOffByEmployeeId(id);}

    public void deleteDaysOff(int daysOffId){
        repository.deleteDaysOff(daysOffId);
    }
}
