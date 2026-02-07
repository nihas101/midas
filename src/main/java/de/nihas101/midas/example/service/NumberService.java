package de.nihas101.midas.example.service;

import de.nihas101.midas.example.entity.NumberEntity;
import de.nihas101.midas.example.repository.NumberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NumberService implements NumberReader, NumberWriter {

    private final NumberRepository numberRepository;

    public NumberService(NumberRepository numberRepository) {
        this.numberRepository = numberRepository;
    }

    @Override
    public List<NumberEntity> getAllNumbers() {
        return numberRepository.findAll();
    }

    @Override
    public void addNumber(Integer value) {
        NumberEntity numberEntity = new NumberEntity(value);
        numberRepository.save(numberEntity);
    }

    @Override
    public Integer getSum() {
        return numberRepository.findAll().stream()
                .map(NumberEntity::getValue)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
