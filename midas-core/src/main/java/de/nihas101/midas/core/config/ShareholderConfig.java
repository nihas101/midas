package de.nihas101.midas.core.config;

import de.nihas101.midas.api.DeleteMode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShareholderConfig {

    private DeleteMode deleteMode;

    public ShareholderConfig() {
        this(DeleteMode.CASCADE);
    }
}
