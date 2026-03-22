package de.nihas101.midas.shareholders.entity;

import de.nihas101.midas.shareholders.dto.Shareholder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "shareholders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareholderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "display_id", insertable = false)
    @Generated(event = {EventType.INSERT})
    private Integer displayId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

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
