package de.nihas101.midas.api.shareholder;

public interface Shareholder {

    Integer getId();

    Integer getDisplayId();

    String getFirstName();

    String getLastName();

    void setLastName(String lastName);

    void setFirstName(String firstName);

    void setId(Integer id);

    void setDisplayId(Integer displayId);
}
