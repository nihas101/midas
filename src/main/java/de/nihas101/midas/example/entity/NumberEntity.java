package de.nihas101.midas.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "numbers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int value;

    public NumberEntity(Integer value) {
        this.value = value;
    }
}
