package com.bioinnovate.grh.data.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
public class Employee extends User {

    private String firstName;
    private String lastName;
    private int codeCnss;
    private int cin;
    private int phone;
    private double salary;
    private double daysOffLeft;
    private String position;
    private String contractPapers;
    private String paperWork;
    private Boolean isInDaysOff;
    private Date startWorkDate;
    @OneToOne
    private Employee substitute;
    @OneToOne
    @JoinColumn(name = "department_id")
    private Department department;
    private String picture;
    private Boolean gender;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Delays> delays;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private  List<Absences> absences;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<DaysOff> daysOff;
    private String contract;

    public Employee() {
    }

    public Date getStartWorkDate() {
        return startWorkDate;
    }

    public void setStartWorkDate(Date startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    public Boolean getInDaysOff() {
        return isInDaysOff;
    }

    public void setInDaysOff(Boolean inDaysOff) {
        isInDaysOff = inDaysOff;
    }

    public Employee getSubstitute() {
        return substitute;
    }

    public void setSubstitute(Employee substitute) {
        this.substitute = substitute;
    }

    public String getContractPapers() {
        return contractPapers;
    }

    public void setContractPapers(String contractPapers) {
        this.contractPapers = contractPapers;
    }

    public String getPaperWork() {
        return paperWork;
    }

    public void setPaperWork(String paperWork) {
        this.paperWork = paperWork;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public List<DaysOff> getDaysOff() {
        return daysOff;
    }

    public void setDaysOff(List<DaysOff> daysOff) {
        this.daysOff = daysOff;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public int getCodeCnss() {
        return codeCnss;
    }

    public void setCodeCnss(int codeCnss) {
        this.codeCnss = codeCnss;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getDaysOffLeft() {
        return daysOffLeft;
    }

    public void setDaysOffLeft(double daysOffLeft) {
        this.daysOffLeft = daysOffLeft;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String post) {
        this.position = post;
    }
    
    public List<Delays> getDelays() {
        return delays;
    }

    public void setDelays(List<Delays> delays) {
        this.delays = delays;
    }
    
    public List<Absences> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absences> absences) {
        this.absences = absences;
    }
}
