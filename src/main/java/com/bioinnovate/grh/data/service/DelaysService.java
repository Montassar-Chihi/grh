package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.Delays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.List;

@Service
public class DelaysService extends CrudService<Delays, Integer> {
    private DelaysRepository repository;

    public DelaysService(@Autowired DelaysRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DelaysRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public Integer findTotalDelays(){return repository.findTotalDelays();}

    public void deleteDelays(int delaysId){
        repository.deleteDelays(delaysId);
    }
}
