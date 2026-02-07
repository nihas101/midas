package de.nihas101.midas.userconfig.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_config")
@Getter
@Setter
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_identifier", nullable = false, unique = true)
    private String userIdentifier;

    private String theme;

    private String locale;

    public UserConfig() {
    }

    public UserConfig(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
}
