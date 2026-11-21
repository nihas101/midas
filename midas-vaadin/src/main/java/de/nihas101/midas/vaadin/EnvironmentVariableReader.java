package de.nihas101.midas.vaadin;

public class EnvironmentVariableReader {

    public String getEnvironmentVariable(final String environmentVariable) {
        return System.getenv(environmentVariable);
    }
}