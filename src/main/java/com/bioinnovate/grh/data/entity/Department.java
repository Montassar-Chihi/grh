package com.bioinnovate.grh.data.entity;

import com.bioinnovate.grh.data.AbstractEntity;

import javax.persistence.Entity;
import java.sql.Date;

@Entity
public class Department extends AbstractEntity {

    private String name;
    private Date dateRedZone;
    private int durationRedZone;

    public Department() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateRedZone() {
        return dateRedZone;
    }

    public void setDateRedZone(Date dateRedZone) {
        this.dateRedZone = dateRedZone;
    }

    public int getDurationRedZone() {
        return durationRedZone;
    }

    public void setDurationRedZone(int durationRedZone) {
        this.durationRedZone = durationRedZone;
    }
}
