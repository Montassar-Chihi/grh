package com.bioinnovate.grh.data.entity;

import com.bioinnovate.grh.data.AbstractEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Absences extends AbstractEntity {

    private Date date;
    private double duration;
    private Boolean justified;

    public Absences() {
    }

    public Boolean getJustified() {
        return justified;
    }

    public void setJustified(Boolean justified) {
        this.justified = justified;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

}
