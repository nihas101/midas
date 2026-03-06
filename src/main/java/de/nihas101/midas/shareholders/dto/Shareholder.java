package de.nihas101.midas.shareholders.dto;

import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shareholder {

    private Integer id;

    private int displayId;

    private String firstName;

    private String lastName;

    private Integer externalId;

    public static Shareholder fromEntity(ShareholderEntity entity) {
        return new Shareholder(
                entity.getId(),
                entity.getDisplayId() != null ? entity.getDisplayId() : entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getExternalId()
        );
    }
}
