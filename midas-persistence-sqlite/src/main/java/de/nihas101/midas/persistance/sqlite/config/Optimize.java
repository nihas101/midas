package de.nihas101.midas.persistance.sqlite.config;

import lombok.Data;

@Data
public class Optimize {
    private boolean enabled = true;
    private Vacuum vacuum = new Vacuum();

}
