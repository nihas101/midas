package de.nihas101.midas.core.shareholders.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultShareholder implements Shareholder {

    private Integer id;

    private Integer displayId;

    private String firstName;

    private String lastName;

    public static Shareholder fromEntity(ShareholderEntity entity) {
        return new DefaultShareholder(
                entity.getId(),
                entity.getDisplayId() != null ? entity.getDisplayId() : entity.getId(),
                entity.getFirstName(),
                entity.getLastName()
        );
    }
}
