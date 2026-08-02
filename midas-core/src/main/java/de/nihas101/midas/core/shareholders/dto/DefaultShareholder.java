package de.nihas101.midas.core.shareholders.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
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

    public static ShareholderEntity fromDto(final Shareholder shareholder) {
        if (shareholder == null) {
            return null;
        }

        return new ShareholderEntity(
                shareholder.getId(),
                shareholder.getDisplayId(),
                shareholder.getFirstName(),
                shareholder.getLastName()
        );
    }
}
