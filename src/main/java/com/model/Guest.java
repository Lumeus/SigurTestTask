package com.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@Entity
public class Guest extends Person{
    private LocalDate visitDate;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
