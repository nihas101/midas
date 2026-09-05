package de.nihas101.midas.persistance.sqlite.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vacuum {
    private boolean enabled = true;
    private Duration interval = Duration.ofDays(30);
}
