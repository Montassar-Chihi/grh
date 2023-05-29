package com.bioinnovate.grh.data.entity;

import com.bioinnovate.grh.data.AbstractEntity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Delays extends AbstractEntity {

    private Date date;
    private int duration;

    public Delays() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
