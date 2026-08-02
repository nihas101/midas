package de.nihas101.midas.core.lock.dto;

import de.nihas101.midas.core.lock.entity.LockEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lock { // TODO: Make an interface

    private Integer id;
    private Integer shareholderId;
    private Year year;

    public static Lock fromEntity(LockEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Lock(
                entity.getId(),
                entity.getShareholder() != null
                        ? entity.getShareholder().getId()
                        : null,
                entity.getYear() != null
                        ? Year.of(entity.getYear())
                        : null
        );
    }
}
