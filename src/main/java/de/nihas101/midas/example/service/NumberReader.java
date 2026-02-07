package de.nihas101.midas.example.service;

import de.nihas101.midas.example.entity.NumberEntity;

import java.util.List;

public interface NumberReader {
    List<NumberEntity> getAllNumbers();

    Integer getSum();
}
