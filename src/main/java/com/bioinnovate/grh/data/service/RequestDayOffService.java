package com.bioinnovate.grh.data.service;

import com.bioinnovate.grh.data.entity.RequestDayOff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.sql.Date;
import java.util.List;
@Service
public class RequestDayOffService extends CrudService<RequestDayOff, Integer> {
    private RequestDayOffRepository repository;

    public RequestDayOffService(@Autowired RequestDayOffRepository repository) {
        this.repository = repository;
    }

    @Override
    protected RequestDayOffRepository getRepository() {
        return repository;
    }

    public List findAll(){
        return  repository.findAll();
    }

    public List<RequestDayOff> findRequestDayOffByEmployee(int id){return repository.findRequestDayOffByEmployeeId(id);}

    public List<RequestDayOff> findRequestDayOffByDepartmentId(int id){return repository.findRequestDayOffByDepartmentId(id);}

    public List<RequestDayOff> findRequestDayOffByDepartmentIdAndDateBegin(int id, Date date){
        return repository.findRequestDayOffByDepartmentIdAndDateBegin(id,date);
    }

    public void deleteRequestDayOff(int requestDayOffId){
        repository.deleteRequestDayOff(requestDayOffId);
    }
}
