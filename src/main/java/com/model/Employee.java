package com.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@Entity
public class Employee extends Person{
    private LocalDate hireTime;
    private LocalDate firedTime;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
