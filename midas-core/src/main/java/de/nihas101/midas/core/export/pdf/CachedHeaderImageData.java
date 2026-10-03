package de.nihas101.midas.core.export.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class CachedHeaderImageData implements HeaderImageResolver {
    private final HeaderImageData headerImageData;
    private String cachedDataUri;

    @Override
    public String dataUri() {
        if (StringUtils.isNotBlank(cachedDataUri)) {
            return cachedDataUri;
        }

        this.cachedDataUri = headerImageData.dataUri();
        return cachedDataUri;
    }
}