package de.nihas101.midas.userconfig.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_config")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_identifier", nullable = false, unique = true)
    private String userIdentifier;

    private String theme;

    private String locale;

    public UserConfig(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
}
