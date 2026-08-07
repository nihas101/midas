package de.nihas101.midas.core.shareholders.dto;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholderFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultShareholderFactory implements ShareholderFactory {

    @Override
    public Shareholder create() {
        return new DefaultShareholder();
    }
}
