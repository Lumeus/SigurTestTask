package com.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
public class Department {
    @Id
    private UUID id = UUID.randomUUID();
    private String name;
}
