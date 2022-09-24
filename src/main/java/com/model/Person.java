package com.model;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {
    public enum Type {
        EMPLOYEE,
        GUEST
    }

    @Id
    private UUID id = UUID.randomUUID();
    private UUID card = UUID.randomUUID();
    private Type type;
}
