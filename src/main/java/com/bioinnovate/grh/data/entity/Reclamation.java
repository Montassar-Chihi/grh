package com.bioinnovate.grh.data.entity;

import com.bioinnovate.grh.data.AbstractEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Reclamation extends AbstractEntity {
    private Date date;
    private String Message;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private boolean done;

    public Reclamation() {
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}